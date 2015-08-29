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

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MavenAnt;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.MojoUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.orfjackal.retrolambda.maven.ProcessMainClassesMojo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Retrolambda main classes.
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-retrolambda-dependencies", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE )
public class RetrolambdaDependenciesMojo extends ProcessMainClassesMojo {

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private AndroidConf androidConf;

    @Component
    private MavenAnt ant;

    @Override
    public void execute() throws MojoExecutionException {
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.addAll( mobileConf.getProject().getArtifacts() );
        artifacts.addAll( androidConf.getUserArtifacts() );

        MojoUtils mojo = new MojoUtils( this );

        for ( Artifact artifact : artifacts ) {
            if ( ArtifactResolver.isJava8( artifact ) ) {
                if ( androidConf.isProcessJava8Dependencies() ) {
                    getLog().info( "Retrolambda Java 8 dependency " + artifact );
                    String folderName = artifact.toString().replace( ":", "_" );
                    String jarName = folderName + ".jar";

                    Path folderPath = fs.path( AndroidTarget.UNPACKDIR ).resolve( folderName );
                    folderPath.toFile().mkdirs();
                    ant.uncompress( artifact.getFile(), folderPath );
                    mojo.setParam( "mainInputDir", folderPath.toFile() );
                    mojo.setParam( "mainOutputDir", folderPath.toFile() );
                    mojo.setParam( "defaultMethods", true );
                    super.execute();

                    Path jarPath = fs.path( AndroidTarget.UNPACKDIR ).resolve( jarName );
                    ant.jar( folderPath, jarPath );

                    androidConf.getRetrolambdaArtifacts().put( artifact, jarPath.toFile() );
                }
                else {
                    getLog().warn( "Java 8 dependency: " + artifact + ". Some Java 8 feature (lambdas, stream API, defender methods, ecc.) are not supported. "
                            + "If you have problems with this dependency try with processJava8Dependencies=true" );
                }
            }
        }
    }
}
