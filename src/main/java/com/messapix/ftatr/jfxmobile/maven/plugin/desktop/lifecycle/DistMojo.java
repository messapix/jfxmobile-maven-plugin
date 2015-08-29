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
package com.messapix.ftatr.jfxmobile.maven.plugin.desktop.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.JarMerger;
import com.messapix.ftatr.jfxmobile.maven.plugin.MavenAnt;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.desktop.DesktopConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.desktop.DesktopTarget;
import java.io.File;
import java.util.LinkedHashSet;
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
@Mojo( name = "desktop-dist", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM )
public class DistMojo extends AbstractMojo {
    @Component
    private MavenAnt ant;

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private DesktopConf desktopConf;

    @Component
    private ArtifactResolver resolver;

    @Component
    private JarMerger merger;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Create uberjar
        merger.merge( getJars(), fs.file( DesktopTarget.UBERJAR ), false );
        ant.addToManifest( fs.path( DesktopTarget.UBERJAR ), "Main-Class", mobileConf.getMainClass() );

        // Create app with dependencies
        ant.copy( fs.path( DesktopTarget.JAR ), fs.path( DesktopTarget.APPJAR ) );

        String classpath = "";
        for ( Artifact artifact : mobileConf.getProject().getArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                ant.copy( artifact.getFile().toPath(), fs.path( DesktopTarget.APPLIBDIR ).resolve( artifact.getFile().getName() ) );
                classpath += "lib/" + artifact.getFile().getName() + " ";
            }
        }

        for ( Artifact artifact : desktopConf.getUserArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                ant.copy( artifact.getFile().toPath(), fs.path( DesktopTarget.APPLIBDIR ).resolve( artifact.getFile().getName() ) );
                classpath += "lib/" + artifact.getFile().getName() + " ";
            }
        }

        ant.addToManifest( fs.path( DesktopTarget.APPJAR ), "Class-Path", classpath );
        ant.addToManifest( fs.path( DesktopTarget.APPJAR ), "Main-Class", mobileConf.getMainClass() );
    }

    private Set<File> getJars() {
        Set<File> jars = new LinkedHashSet<>();
        jars.add( mobileConf.getProject().getArtifact().getFile() );
        jars.add( fs.file( DesktopTarget.CLASSESJAR ) );
        jars.add( fs.file( DesktopTarget.RESOURCESJAR ) );

        for ( Artifact artifact : mobileConf.getProject().getArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                jars.add( artifact.getFile() );
            }
        }

        for ( Artifact artifact : desktopConf.getUserArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                jars.add( artifact.getFile() );
            }
        }
        return jars;
    }
}
