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
package com.messapix.ftatr.jfxmobile.maven.plugin.android;

import com.android.ide.common.signing.CertificateInfo;
import com.android.sdklib.repository.FullRevision;
import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.ModelInitializator;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.model.AndroidData;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.model.PackagingOptions;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.model.SigningConfig;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = AndroidConf.class )
public class AndroidConf {

    public static final FullRevision MIN_BUILD_TOOLS_VERSION = new FullRevision( 2, 1, 1 );

    private Path androidSdkDir;

    private Path dalvikSdkDir;

    private Path dalvikSdkLibDir;

    private String buildToolsVersion;

    private Path buildToolsDir;

    private Path buildToolsLibDir;

    private Path platformToolsDir;

    private Path androidJar;

    private String applicationPackage;

    private String compileSdkVersion;

    private String minSdkVersion;

    private String targetSdkVersion;

    private CertificateInfo certificate;

    private PackagingOptions packagingOptions;

    private final List<Artifact> userArtifacts = new ArrayList<>();

    private final List<Artifact> buildingArtifacts = new ArrayList<>();

    private final Map<Artifact, File> retrolambdaArtifacts = new HashMap<>();

    private AndroidData androidData;

    private boolean debugMode = true;

    private boolean configured = false;

    @Requirement
    private Logger log;

    @Requirement
    private MobileConf mobileConf;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private AndroidDependencies deps;

    @Requirement
    private ModelInitializator modelInitializator;

    @Requirement
    private FileSystem fs;

    public void configure( AndroidData androidData ) throws MojoExecutionException, MojoFailureException {
        log.debug( "Configuring build for Android" );

        modelInitializator.process( androidData, "jfxmobile.android" );

        this.androidData = androidData;

        this.packagingOptions = androidData.getPackagingOptions();
        this.minSdkVersion = androidData.getMinSdkVersion();
        this.targetSdkVersion = androidData.getTargetSdkVersion();

        fs.put( AndroidSource.MANIFEST, androidData.getManifest() );
        fs.put( AndroidSource.NATIVEDIR, androidData.getNativeDir() );
        fs.put( AndroidSource.ASSETSDIR, androidData.getAssetsDir() );
        fs.put( AndroidSource.RESDIR, androidData.getResDir() );

        configureAndroidHome( androidData.getAndroidSdk() );
        configureBuildTools( androidData.getBuildToolsVersion() );
        configurePlatform( androidData.getCompileSdkVersion() );
        configureDalvikHome( androidData.getDalvikSdk() );
        configureApplicationPackage( androidData.getApplicationPackage() );
        configureSigning( androidData.getSigningConfig() );
        configureDependencies( androidData.getDependencies() );

        configured = true;
    }

    public Path getAndroidSdkDir() {
        return androidSdkDir;
    }

    public Path getDalvikSdkDir() {
        return dalvikSdkDir;
    }

    public Path getDalvikSdkLibDir() {
        return dalvikSdkLibDir;
    }

    public String getBuildToolsVersion() {
        return buildToolsVersion;
    }

    public Path getBuildToolsDir() {
        return buildToolsDir;
    }

    public Path getBuildToolsLibDir() {
        return buildToolsLibDir;
    }

    public String getApplicationPackage() {
        return applicationPackage;
    }

    public String getCompileSdkVersion() {
        return compileSdkVersion;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public boolean isResourceFiltering() {
        return androidData.isResourceFiltering();
    }

    public boolean isProcessJava8Dependencies() {
        return androidData.isProcessJava8Dependencies();
    }

    public CertificateInfo getCertificate() {
        return certificate;
    }

    public File getBuildTool( String name ) {
        return getBuildToolsDir().resolve( nameWithExtension( name ) ).toFile();
    }

    public File getPlatformTool( String name ) {
        return platformToolsDir.resolve( nameWithExtension( name ) ).toFile();
    }

    public Path getAndroidJar() {
        return androidJar;
    }

    public PackagingOptions getPackagingOptions() {
        return packagingOptions;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public List<Artifact> getUserArtifacts() {
        return userArtifacts;
    }

    public List<Artifact> getBuildingArtifacts() {
        return buildingArtifacts;
    }

    public Map<Artifact, File> getRetrolambdaArtifacts() {
        return retrolambdaArtifacts;
    }

    public boolean isConfigured() {
        return configured;
    }

    private void configureAndroidHome( File androidSdkParam ) throws MojoExecutionException {
        if ( androidSdkParam == null ) {
            log.debug( "No Android SDK defined by the user. Trying with ANDROID_HOME" );

            String env = System.getenv( "ANDROID_HOME" );

            if ( env != null ) {
                this.androidSdkDir = Paths.get( env );
                log.debug( "Android SDK Home = " + this.androidSdkDir );
            }
            else {
                throw new MojoExecutionException( "No Android SDK was found. Set jfxmobile.android.androidSdk property or ANDROID_HOME anvironment variable" );
            }
        }
        else {
            this.androidSdkDir = androidSdkParam.toPath();
        }

        if ( !this.androidSdkDir.toFile().exists() ) {
            throw new MojoExecutionException( "The Android SDK folder " + this.androidSdkDir + " does not exists" );
        }
    }

    private void configureBuildTools( String buildToolsVersionParam ) throws MojoExecutionException {
        Path buildToolsFolder = this.androidSdkDir.resolve( "build-tools" );
        if ( !buildToolsFolder.toFile().exists() ) {
            throw new MojoExecutionException( "Configured Android SDK is invalid. Please install Android Build Tools." );
        }

        if ( buildToolsVersionParam == null || buildToolsVersionParam.trim().isEmpty() ) {
            log.debug( "There was no buildToolsVersion specified, looking for most recent installed version automatically" );

            Path maxRevisionDir = null;
            FullRevision maxRevision = null;
            for ( String dir : buildToolsFolder.toFile().list() ) {
                try {
                    FullRevision revision = FullRevision.parseRevision( dir );

                    if ( revision.isPreview() ) {
                        log.debug( "Ignoring directory " + dir + " as it denotes a preview build tools version" );
                    }
                    else if ( maxRevision == null || maxRevision.compareTo( revision ) < 0 ) {
                        maxRevision = revision;
                        maxRevisionDir = buildToolsFolder.resolve( dir );
                    }
                }
                catch ( NumberFormatException ex ) {
                    log.debug( "Ignoring directory " + dir + " as it does not denote a valid android build tools revision number" );
                }
            }

            if ( maxRevision == null ) {
                throw new MojoExecutionException( "No valid build tools version could be detected in " + androidSdkDir + ". Please check your androidSdk installation." );
            }
            else {
                this.buildToolsVersion = maxRevision.toString();
                this.buildToolsDir = maxRevisionDir;
                log.debug( "Using the following automatically detected buildToolsVersion: " + this.buildToolsVersion );
            }
        }
        else {
            this.buildToolsDir = buildToolsFolder.resolve( buildToolsVersionParam );

            if ( !this.buildToolsDir.toFile().exists() ) {
                throw new MojoExecutionException( "Configured build tools " + buildToolsVersionParam + " is not installed" );
            }

            this.buildToolsVersion = buildToolsVersionParam;
        }

        // Check the build tools version
        if ( FullRevision.parseRevision( buildToolsVersion ).compareTo( MIN_BUILD_TOOLS_VERSION ) < 0 ) {
            throw new MojoExecutionException( "Android buildToolsVersion should be at least version " + MIN_BUILD_TOOLS_VERSION.toString() + ", currently using " + this.buildToolsVersion );
        }

        // Check if android build tools version is valid
        this.buildToolsLibDir = this.getBuildToolsDir().resolve( "lib" );
        if ( !this.buildToolsDir.resolve( "aapt" ).toFile().exists() && !this.buildToolsDir.resolve( "aapt.exe" ).toFile().exists() ) {
            throw new MojoExecutionException( "Configured buildToolsVersion is invalid: " + this.buildToolsVersion + " (" + getBuildToolsDir() + ")" );
        }
    }

    private void configurePlatform( String compileSdkVersionParam ) throws MojoExecutionException {
        Path platformsFolder = androidSdkDir.resolve( "platforms" );

        if ( compileSdkVersionParam == null || compileSdkVersionParam.trim().isEmpty() ) {
            log.debug( "There was no platform specified, looking for most recent installed version automatically" );

            int version = 0;
            for ( String dir : platformsFolder.toFile().list() ) {
                if ( dir.startsWith( "android-" ) ) {
                    try {
                        int v = Integer.parseInt( dir.substring( "android-".length() ) );
                        if ( v > version ) {
                            version = v;
                        }
                    }
                    catch ( NumberFormatException ex ) {
                        log.debug( "Ignoring directory " + dir + " as it does not denote a valid android platform version number" );
                    }
                }
            }

            if ( version == 0 ) {
                throw new MojoExecutionException( "No valid Android platform is installed" );
            }

            this.compileSdkVersion = version + "";
        }
        else {
            if ( !platformsFolder.resolve( "android-" + compileSdkVersionParam ).toFile().exists() ) {
                throw new MojoExecutionException( "Configured compileSdkVersion is invalid: " + compileSdkVersionParam );
            }

            this.compileSdkVersion = compileSdkVersionParam;
        }

        this.androidJar = platformsFolder.resolve( "android-" + this.compileSdkVersion ).resolve( "android.jar" );
        this.platformToolsDir = androidSdkDir.resolve( "platform-tools" );

        log.debug( "SDK version " + this.compileSdkVersion + " is used" );
    }

    private void configureDalvikHome( File dalvikSdkParam ) throws MojoExecutionException {
        if ( dalvikSdkParam != null ) {
            if ( dalvikSdkParam.exists() ) {
                this.dalvikSdkDir = dalvikSdkParam.toPath();
                this.dalvikSdkLibDir = this.dalvikSdkDir.resolve( "rt/lib" );

                if ( !dalvikSdkLibDir.toFile().exists() ) {
                    throw new MojoExecutionException( "Configured dalvikSdk is invalid: " + dalvikSdkParam );
                }
            }
            else {
                throw new MojoExecutionException( "The Dalvik Sdk Home '" + dalvikSdkParam + "' does not exist" );
            }
        }
        else {
            dalvikSdkDir = artifactResolver.unpack( deps.dalvikSdk() );
            dalvikSdkLibDir = dalvikSdkDir.resolve( "rt/lib" );
        }

        log.debug( "Dalvik SDK Home = " + this.dalvikSdkDir );
    }

    private void configureApplicationPackage( String applicationPackageParam ) {
        if ( applicationPackageParam == null ) {
            int pos = mobileConf.getMainClass().lastIndexOf( "." );
            this.applicationPackage = pos != -1 ? mobileConf.getMainClass().substring( 0, pos ) : mobileConf.getMainClass();
        }
        else {
            this.applicationPackage = applicationPackageParam;
        }
    }

    private void configureSigning( SigningConfig signingConfigParam ) throws MojoExecutionException {
        SigningConfig signingConfig;

        if ( signingConfigParam == null ) {
            signingConfig = SigningConfig.createDebugSigningConfig();
        }
        else {
            signingConfig = signingConfigParam;
        }

        if ( !signingConfig.isSigningReady() ) {
            throw new MojoExecutionException( "SigningConfig not propertly configured" );
        }

        if ( !signingConfig.getStoreFile().exists() && signingConfig.isDebugKeystore() ) {
            boolean result = signingConfig.createDebugStore();
            if ( !result ) {
                throw new MojoExecutionException( "Unable to create missing debug keystore." );
            }
        }

        this.certificate = signingConfig.createCertificate();

        if ( this.certificate == null ) {
            throw new MojoExecutionException( "Failed to read key from keystore" );
        }

        debugMode = signingConfig.isDebugKeystore();
    }

    private static String nameWithExtension( String name ) {
        return System.getProperty( "os.name" ).startsWith( "Windows" ) ? name + ".exe" : name;
    }

    private void configureDependencies( List<Dependency> dependencies ) throws MojoExecutionException {
        // Add Android specific dependencies
        if ( dependencies != null ) {
            for ( Dependency dependency : dependencies ) {
                userArtifacts.add( artifactResolver.resolve( dependency ) );
            }
        }

        // Add Android building dependencies
        buildingArtifacts.add( artifactResolver.resolve( deps.multidex() ) );
        buildingArtifacts.add( artifactResolver.resolve( deps.jfxrt() ) );
        buildingArtifacts.add( artifactResolver.resolve( deps.compat() ) );
        buildingArtifacts.add( artifactResolver.resolve( deps.android() ) );
        buildingArtifacts.add( artifactResolver.resolve( deps.jfxDvk() ) );
    }
}
