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

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = Executor.class )
public class Executor {
    public static final String OUTPUT = "out";

    public static final String ERROR = "err";

    @Requirement
    private MavenAnt ant;

    @Requirement
    private Logger log;

    public void java( File jar, String className, File outputFile, String jvmArgs, String... args ) throws MojoExecutionException {
        Java java = new Java();

        java.setProject( ant.getAntProject() );
        java.setClasspath( buildPath( jar.toString() ) );
        java.setClassname( className );
        java.setFork( true );

        if ( jvmArgs != null ) {
            java.createJvmarg().setValue( jvmArgs );
        }

        if ( java.getProject().getProperty( OUTPUT ) == null ) {
            java.setOutputproperty( OUTPUT );
            java.setErrorProperty( ERROR );
        }

        for ( String arg : args ) {
            if ( arg != null && !arg.trim().isEmpty() ) {
                java.createArg().setValue( arg );
            }
        }

        int result = java.executeJava();

        if ( result != 0 ) {
            throw new MojoExecutionException( java.getProject().getProperty( ERROR ) );
        }
        else {
            if ( outputFile != null ) {
                try {
                    Files.write( java.getProject().getProperty( OUTPUT ), outputFile, Charset.forName( "UTF-8" ) );
                }
                catch ( IOException ex ) {
                    throw new MojoExecutionException( "Error", ex );
                }
            }
        }
    }

    public String exe( File executable, String... args ) throws MojoExecutionException {
        StringBuilder cmdBuilder = new StringBuilder( executable.toString() );

        for ( String arg : args ) {
            cmdBuilder.append( " " ).append( arg.trim() );
        }

        try {
            Process p = Runtime.getRuntime().exec( cmdBuilder.toString() );
            p.waitFor();

            String output = fromStream( p.getInputStream() );
            String error = fromStream( p.getErrorStream() );

            if ( log.isDebugEnabled() ) {
                log.debug( output );
            }

            if ( p.exitValue() != 0 ) {
                log.error( error );
                throw new MojoExecutionException( "Cannot execute " + executable.toPath().getFileName() );
            }

            return output;
        }
        catch ( IOException | InterruptedException ex ) {
            throw new MojoExecutionException( "Error ", ex );
        }
    }

    private Path buildPath( String file ) {
        Path path = new Path( ant.getAntProject() );
        path.setPath( file );
        return path;
    }

    private String fromStream( InputStream stream ) throws MojoExecutionException {
        try {
            BufferedReader outReader = new BufferedReader( new InputStreamReader( stream ) );
            StringBuilder builder = new StringBuilder();
            String line;
            while ( ( line = outReader.readLine() ) != null ) {
                builder.append( line ).append( "\n" );
            }

            return builder.toString();
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Error during read output" );
        }
    }
}
