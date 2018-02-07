/*
 * Copyright 2016 Messapix.
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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MavenAnt;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.EmbeddedConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.EmbeddedTarget;
import java.nio.file.Path;
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
@Mojo( name = "embedded-dist", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM )
public class DistMojo extends AbstractMojo {
    @Component
    private MavenAnt ant;

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private EmbeddedConf embeddedConf;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Path libsPath = fs.path( EmbeddedTarget.DISTDIR ).resolve( "libs" );
        String classpath = "";
        
        for ( Artifact artifact : mobileConf.getProject().getArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                ant.copy( artifact.getFile().toPath(), libsPath.resolve( artifact.getFile().getName() ) );
                classpath += "libs/" + artifact.getFile().getName() + " ";
            }
        }

        for ( Artifact artifact : embeddedConf.getUserArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                ant.copy( artifact.getFile().toPath(), libsPath.resolve( artifact.getFile().getName() ) );
                classpath += "libs/" + artifact.getFile().getName() + " ";
            }
        }
        
        classpath = classpath.trim();

        if ( !classpath.isEmpty() ) {
            ant.addToManifest( fs.path( EmbeddedTarget.JAR ), "Class-Path", classpath );
        }
    }
}
