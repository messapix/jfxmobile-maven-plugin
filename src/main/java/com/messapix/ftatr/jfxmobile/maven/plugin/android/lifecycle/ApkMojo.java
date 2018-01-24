/*
 * Copyright 2015 Messapix.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.messapix.ftatr.jfxmobile.maven.plugin.android.lifecycle;

import com.android.builder.internal.packaging.Packager;
import com.android.builder.model.PackagingOptions;
import com.android.builder.packaging.DuplicateFileException;
import com.android.builder.packaging.PackagerException;
import com.android.builder.packaging.SealedPackageException;
import com.android.utils.NullLogger;
import com.messapix.ftatr.jfxmobile.maven.plugin.Executor;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidSource;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-apk", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class ApkMojo extends AbstractMojo {
    @Component
    private MobileConf mobileConf;

    @Component
    private AndroidConf androidConf;

    @Component
    private Executor executor;

    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Packager packager = buildPackager( androidConf.getPackagingOptions() );

            List<Artifact> artifacts = new ArrayList<>();
            artifacts.add( mobileConf.getProject().getArtifact() );
            artifacts.addAll( mobileConf.getProject().getArtifacts() );
            artifacts.addAll( androidConf.getUserArtifacts() );
            artifacts.addAll( androidConf.getBuildingArtifacts() );

            for ( Artifact artifact : artifacts ) {
                if ( checkArtifact( artifact ) ) {
                    getLog().debug( "apk: adding " + artifact + " to packager.addResourcesFromJar()" );
                    try {
                        packager.addResourcesFromJar( artifact.getFile() );
                    }
                    catch ( DuplicateFileException ex ) {
                        getLog().warn( "Duplicate resources found. It can take a while to get the complete list of all duplicates." );
                        Set<String> duplicates = new HashSet<>();
                        checkForAllDuplicates(
                                artifacts,
                                duplicates,
                                androidConf.getPackagingOptions() != null
                                        ? androidConf.getPackagingOptions()
                                        : new com.messapix.ftatr.jfxmobile.maven.plugin.android.model.PackagingOptions()
                        );
                    }
                }
            }

            File nativeDir = fs.file( AndroidSource.NATIVEDIR );
            if ( nativeDir.exists() ) {
                packager.addNativeLibraries( nativeDir, null );
            }

            packager.addNativeLibraries( androidConf.getDalvikSdkLibDir().toFile(), null );

            packager.sealApk();

            executor.exe( androidConf.getBuildTool( "zipalign" ),
                          "-f", "4",
                          fs.string( AndroidTarget.UNALIGNEDAPK ),
                          fs.string( androidConf.isDebugMode() ? AndroidTarget.DEBUGAPK : AndroidTarget.APK )
            );
        }

        catch ( DuplicateFileException | SealedPackageException | PackagerException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    private Packager buildPackager( PackagingOptions options ) throws MojoExecutionException {
        try {
            Packager packager = new Packager(
                    fs.string( AndroidTarget.UNALIGNEDAPK ),
                    fs.string( AndroidTarget.RESOURCESAPK ),
                    androidConf.getCertificate(),
                    null,
                    options,
                    NullLogger.getLogger()
            );

            packager.addDexFiles( fs.file( AndroidTarget.DEXDIR ), new ArrayList<File>() );
            packager.setJniDebugMode( false );

            packager.addResourcesFromJar( fs.file( AndroidTarget.RESOURCESJAR ) );

            return packager;
        }
        catch ( PackagerException | DuplicateFileException | SealedPackageException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    private boolean checkArtifact( Artifact artifact ) {
        if ( artifact == null ) {
            return false;
        }

        File file = artifact.getFile();
        return file != null
                && file.getName().endsWith( ".jar" )
                && !"android.jar".equals( file.getName() )
                && !Artifact.SCOPE_PROVIDED.equals( artifact.getScope() )
                && !Artifact.SCOPE_TEST.equals( artifact.getScope() );
    }

    private void checkForAllDuplicates( List<Artifact> artifacts, Set<String> duplicates, PackagingOptions options ) throws MojoExecutionException {
        Packager packager = buildPackager( options );

        for ( Artifact artifact : artifacts ) {
            if ( checkArtifact( artifact ) ) {
                try {
                    packager.addResourcesFromJar( artifact.getFile() );
                }
                catch ( DuplicateFileException ex ) {
                    getLog().debug( ex.getMessage() );
                    duplicates.add( ex.getArchivePath() );

                    options.getExcludes().add( ex.getArchivePath() );
                    checkForAllDuplicates( artifacts, duplicates, options );
                    throw new MojoExecutionException( buildDuplucateError( duplicates, options.getExcludes(), options.getPickFirsts() ) );
                }
                catch ( PackagerException | SealedPackageException ex ) {
                    throw new MojoExecutionException( "Error during duplicate checking", ex );
                }
            }
        }
    }

    private String buildDuplucateError( Set<String> duplicates, Set<String> excludes, Set<String> pickFirsts ) {
        StringBuilder sb = new StringBuilder( "The following resources are duplicated in dependencies:\n" );
        for ( String duplicate : duplicates ) {
            sb.append( duplicate ).append( "\n" );
        }

        sb.append( "\n" );

        sb.append( "Use packagingOptions parameter to <exclude> or <pickFirst> such resources.\n" )
                .append( "The following is a possible solution based on settings in pom.xml\n\n" )
                .append( "<android>\n" )
                .append( "<packagingOptions>\n" )
                .append( "<excludes>\n" );

        for ( String exclude : excludes ) {
            sb.append( "<exclude>" ).append( exclude ).append( "</exclude>\n" );
        }

        if ( !pickFirsts.isEmpty() ) {
            sb.append( "<pickFirsts>\n" );
            for ( String pickFirst : pickFirsts ) {
                sb.append( "<pickFirst>" ).append( pickFirst ).append( "</pickFirst>\n" );
            }
            sb.append( "</pickFirsts>\n" );
        }

        sb.append( "</excludes>\n" ).append( "</packagingOptions>\n" ).append( "</android>\n" );

        sb.append( "Re-run maven in debug mode to get more info about these duplicate resources." );

        return sb.toString();
    }
}
