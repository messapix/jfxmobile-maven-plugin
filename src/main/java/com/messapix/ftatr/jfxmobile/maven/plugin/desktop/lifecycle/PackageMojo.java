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
import com.messapix.ftatr.jfxmobile.maven.plugin.desktop.DesktopTarget;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
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
@Mojo( name = "desktop-package", requiresDependencyResolution = ResolutionScope.COMPILE )
public class PackageMojo extends AbstractMojo {
    @Component
    private MavenAnt ant;

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private ArtifactResolver resolver;

    @Component
    private JarMerger merger;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ant.jar( fs.path( DesktopTarget.CLASSESDIR ), fs.path( DesktopTarget.CLASSESJAR ) );
        ant.jar( fs.path( DesktopTarget.RESOURCESDIR ), fs.path( DesktopTarget.RESOURCESJAR ) );

        merger.merge( getJars(), fs.file( DesktopTarget.JAR ), false );
    }

    private Set<File> getJars() {
        Set<File> jars = new LinkedHashSet<>();
        jars.add( mobileConf.getProject().getArtifact().getFile() );
        jars.add( fs.file( DesktopTarget.CLASSESJAR ) );
        jars.add( fs.file( DesktopTarget.RESOURCESJAR ) );
        return jars;
    }
}
