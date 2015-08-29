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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = ArtifactResolver.class )
public class ArtifactResolver {
    private static final String UNPACKED_FOLDER = "unpacked";

    @Requirement
    private MobileExpressionEvaluator evaluator;

    @Requirement
    private ArtifactFactory artifactFactory;

    @Requirement
    private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    @Requirement
    private MavenAnt ant;

    @Requirement
    private Logger logger;

    public Path unpack( Dependency dependency ) throws MojoExecutionException {
        Artifact artifact = resolve( dependency );
        File file = artifact.getFile();
        File unpackedDir = new File( file.getParent(), UNPACKED_FOLDER );

        if ( dependency.getType() != null && ( dependency.getType().equals( "zip" ) || dependency.getType().equals( "tar.gz" ) ) ) {
            if ( unpackedDir.exists() && artifact.isSnapshot() ) {
                unpackedDir.delete();
            }

            if ( !unpackedDir.exists() || unpackedDir.list().length == 0 ) {
                logger.debug( "Trying to unpack artifact " + artifact );

                if ( unpackedDir.exists() && unpackedDir.list().length == 0 ) {
                    unpackedDir.delete();
                }

                if ( !unpackedDir.mkdirs() ) {
                    throw new MojoExecutionException( "Unable to create base directory to unpack into: " + unpackedDir );
                }

                ant.uncompress( file, unpackedDir.toPath() );

                Path unpackedDistDirectory = buildPath( artifact );
                if ( !unpackedDistDirectory.toFile().exists() ) {
                    throw new MojoExecutionException( "Unable to unpack archive" );
                }

                Path binDirectory = unpackedDistDirectory.resolve( "bin" );

                if ( binDirectory.toFile().exists() ) {
                    ant.chmod( binDirectory, "+x", "*" );
                }
            }

            return buildPath( artifact );
        }
        else {
            return file.toPath();
        }

    }

    public Artifact resolve( Dependency dependency ) throws MojoExecutionException {
        logger.debug( "Trying to resolve dependency " + dependency );

        Artifact artifact = createArtifact( dependency );

        if ( Artifact.SCOPE_SYSTEM.equals( dependency.getScope() ) ) {
            artifact.setFile( new File( dependency.getSystemPath() ) );
        }

        return resolve( artifact );
    }

    public Artifact resolve( Artifact artifact ) throws MojoExecutionException {
        logger.debug( "Trying to resolve artifact " + artifact );
        try {
            List<ArtifactRepository> remoteArtifactRepositories = evaluator.evaluate( "${project.remoteArtifactRepositories}", List.class );
            ArtifactRepository localRepository = evaluator.evaluate( "${localRepository}", ArtifactRepository.class );
            resolver.resolve( artifact, remoteArtifactRepositories, localRepository );
        }
        catch ( ArtifactNotFoundException | ArtifactResolutionException | ExpressionEvaluationException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }

        logger.debug( "Artifact " + artifact + " resolved" );

        return artifact;
    }

    public static boolean isJava8( Artifact artifact ) throws MojoExecutionException {
        File file = artifact.getFile();

        if ( file == null ) {
            return false;
        }

        try {
            JarFile jarFile = new JarFile( artifact.getFile() );

            Enumeration<JarEntry> entries = jarFile.entries();

            while ( entries.hasMoreElements() ) {
                JarEntry entry = entries.nextElement();
                if ( entry.getName().endsWith( ".class" ) ) {
                    DataInputStream dataInputStream = new DataInputStream( jarFile.getInputStream( entry ) );
                    if ( dataInputStream.readInt() == 0xCAFEBABE ) {
                        int minor = dataInputStream.readUnsignedShort();
                        int major = dataInputStream.readUnsignedShort();
                        return major == 52;
                    }
                }
            }

            return false;
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Dependencies " + artifact + " cannot be read" );
        }
    }

    private Artifact createArtifact( Dependency dependency ) {
        if ( dependency.getClassifier() == null ) {
            return artifactFactory.createArtifact(
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getScope(),
                    dependency.getType()
            );
        }
        else {
            return artifactFactory.createArtifactWithClassifier(
                    dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getVersion(),
                    dependency.getType(),
                    dependency.getClassifier()
            );
        }
    }

    private Path buildPath( Artifact artifact ) throws MojoExecutionException {
        Path unpackedFolder = artifact.getFile().toPath().resolveSibling( UNPACKED_FOLDER );

        String[] dirs = unpackedFolder.toFile().list();

        if ( dirs.length != 1 ) {
            throw new MojoExecutionException( "Error during installation of the artifact " + artifact );
        }

        return unpackedFolder.resolve( dirs[0] );
    }

}
