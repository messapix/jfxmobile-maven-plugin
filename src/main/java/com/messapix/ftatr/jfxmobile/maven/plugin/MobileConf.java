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

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = MobileConf.class )
public class MobileConf {
    private static final String DEFAULT_JAVAFXPORTS_VERSION = "8u60-b2";

    @AsProperty
    @Descriptor(
             desc = "The version of javafxPorts to use",
             defaultValue = DEFAULT_JAVAFXPORTS_VERSION
    )
    private String javafxportsVersion;

    @AsProperty
    @Descriptor(
             desc = "The fully qualified name of the main class"
    )
    private String mainClass;

    @AsProperty
    @Descriptor(
             desc = "The fully qualified name of the class used as preloader"
    )
    private String preloaderClass;

    @Requirement
    private MobileExpressionEvaluator evaluator;

    @Requirement
    private MavenProject project;

    @Requirement
    private FileSystem fs;

    public void configure( String javafxportsVersion, String mainClass, String preloaderClass ) throws MojoExecutionException {
        this.javafxportsVersion = javafxportsVersion == null || javafxportsVersion.trim().isEmpty() ? DEFAULT_JAVAFXPORTS_VERSION : javafxportsVersion;

        if ( mainClass != null ) {
            this.mainClass = mainClass;
        }
        else {
            throw new MojoExecutionException( "No main class configured" );
        }

        this.preloaderClass = preloaderClass == null || preloaderClass.trim().isEmpty() ? null : preloaderClass;

        // Create base folders
        fs.javafxports().toFile().mkdirs();

        for ( Target target : MainTarget.values() ) {
            if ( target.getType() == Target.Type.DIR ) {
                fs.file( target ).mkdirs();
            }
        }
    }

    public String getJavafxportsVersion() {
        return javafxportsVersion;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getPreloaderClass() {
        return preloaderClass;
    }

    public String getProjectName() {
        try {
            return evaluator.evaluate( "${project.name}" );
        }
        catch ( ExpressionEvaluationException ex ) {
            return null;
        }
    }

    public String getProjectVersion() {
        try {
            return evaluator.evaluate( "${project.version}" );
        }
        catch ( ExpressionEvaluationException ex ) {
            return null;
        }
    }

    public String getBuildFileName() {
        try {
            String fileName = evaluator.evaluate( "${project.build.fileName}" );

            return fileName != null ? fileName : evaluator.evaluate( "${project.artifactId}" );
        }
        catch ( ExpressionEvaluationException ex ) {
            return null;
        }
    }

    public boolean isConfigured() {
        return javafxportsVersion != null;
    }

    public MavenProject getProject() {
        return project.clone();
    }
}
