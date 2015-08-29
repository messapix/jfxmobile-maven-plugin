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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = FileSystem.class )
public class FileSystem {
    @Requirement
    private MobileConf mobileConf;

    @Requirement
    private MobileExpressionEvaluator evaluator;

    private final Map<Source, Path> sourcePaths = new HashMap<>();

    public Path root() {
        try {
            return Paths.get( evaluator.evaluate( "${basedir}" ) );
        }
        catch ( ExpressionEvaluationException ex ) {
            // This should never happen
            return null;
        }
    }

    /**
     * The target targetDir dir.
     *
     * @return
     */
    public Path targetDir() {
        try {
            return Paths.get( evaluator.evaluate( "${project.build.directory}" ) );
        }
        catch ( ExpressionEvaluationException ex ) {
            // This should never happen
            return null;
        }
    }

    /**
     * The absolute path of javafxports target folder
     *
     * @return
     */
    public Path javafxports() {
        return targetDir().resolve( "javafxports" );
    }

    /**
     * Return the path of the target
     *
     * @param target
     * @return
     */
    public Path path( Target target ) {
        String relativePath = target.getRelativePath().replaceAll( "\\$\\{name\\}", mobileConf.getBuildFileName() );
        return javafxports().resolve( relativePath );
    }

    /**
     * Return the File object of the target
     *
     * @param target
     * @return
     */
    public File file( Target target ) {
        return path( target ).toFile();
    }

    /**
     * Return the target's path as a string
     *
     * @param target
     * @return
     */
    public String string( Target target ) {
        return path( target ).toString();
    }

    public void put( Source source, File file ) throws MojoFailureException {
        if ( file == null ) {
            return;
        }

        if ( !file.exists() ) {
            String type = source.getType() == Source.Type.DIR ? "path" : "file";
            throw new MojoFailureException( "Configured " + type + " " + file + " does not exist." );
        }

        sourcePaths.put( source, file.toPath() );
    }

    public Path path( Source source ) {
        if ( sourcePaths.containsKey( source ) ) {
            return sourcePaths.get( source );
        }

        if ( source.getDefaultRelativePath() == null ) {
            return null;
        }

        return root().resolve( source.getDefaultRelativePath() );
    }

    public File file( Source source ) {
        return path( source ).toFile();
    }

    public String string( Source source ) {
        return path( source ).toString();
    }

    public boolean exists( Source source ) {
        return file( source ).exists();
    }

    public String fileNameWithoutExt( Target target ) {
        String fileName = path( target ).getFileName().toString();

        int lastPoint = fileName.lastIndexOf( "." );

        if ( lastPoint != -1 ) {
            return fileName.substring( 0, lastPoint );
        }
        else {
            return fileName;
        }
    }
}
