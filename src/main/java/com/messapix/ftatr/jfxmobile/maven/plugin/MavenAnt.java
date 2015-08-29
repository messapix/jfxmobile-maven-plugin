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
package com.messapix.ftatr.jfxmobile.maven.plugin;

import java.io.File;
import java.nio.file.Path;
import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.Untar;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = MavenAnt.class )
public class MavenAnt {
    @Requirement
    private MavenProject project;

    @Requirement
    private Logger log;

    private Project antProject;

    public void jar( Path dir, Path dest ) {
        Jar jar = new Jar();
        jar.setProject( getAntProject() );
        jar.setBasedir( dir.toFile() );
        jar.setDestFile( dest.toFile() );
        jar.execute();
    }

    public void addToManifest( Path dest, String name, String value ) throws MojoExecutionException {
        try {
            Jar jar = new Jar();
            jar.setProject( getAntProject() );
            jar.setDestFile( dest.toFile() );
            jar.setUpdate( true );
            Manifest manifest = new Manifest();
            manifest.addConfiguredAttribute( new Manifest.Attribute( name, value ) );
            jar.addConfiguredManifest( manifest );
            jar.execute();
        }
        catch ( ManifestException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    public void copy( Path from, Path to ) {
        Copy copy = new Copy();
        copy.setProject( antProject );
        copy.setFile( from.toFile() );
        copy.setTofile( to.toFile() );
        copy.execute();
    }

    public void chmod( Path path, String perms, String includes ) {
        Chmod chmod = new Chmod();
        chmod.setProject( getAntProject() );
        chmod.setDir( path.toFile() );
        chmod.setPerm( perms );
        chmod.setIncludes( includes );
        chmod.execute();
    }

    public void uncompress( File file, Path dest ) {
        if ( file.getName().endsWith( ".zip" ) || file.getName().endsWith( ".jar" ) ) {
            Unzip unzip = new Unzip();
            unzip.setProject( getAntProject() );
            unzip.setSrc( file );
            unzip.setDest( dest.toFile() );
            unzip.setStripAbsolutePathSpec( true );
            unzip.execute();
        }
        else if ( file.getName().endsWith( ".tar.gz" ) ) {
            Untar untar = new Untar();
            untar.setProject( getAntProject() );
            Untar.UntarCompressionMethod ucm = new Untar.UntarCompressionMethod();
            ucm.setValue( "gzip" );
            untar.setCompression( ucm );
            untar.setStripAbsolutePathSpec( true );
            untar.setSrc( file );
            untar.setDest( dest.toFile() );
            untar.execute();
        }
    }

    public Project getAntProject() {
        if ( antProject == null ) {
            antProject = new Project();
            antProject.setName( project.getName() );
            antProject.init();

            DefaultLogger antLogger = new DefaultLogger();
            antLogger.setOutputPrintStream( System.out );
            antLogger.setErrorPrintStream( System.err );
            antLogger.setMessageOutputLevel( log.isDebugEnabled() ? Project.MSG_DEBUG : Project.MSG_INFO );

            antProject.addBuildListener( antLogger );
            antProject.setBaseDir( project.getBasedir() );
        }

        return antProject;
    }
}
