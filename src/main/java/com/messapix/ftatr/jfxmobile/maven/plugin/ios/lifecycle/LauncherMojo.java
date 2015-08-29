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
package com.messapix.ftatr.jfxmobile.maven.plugin.ios.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.TemplateUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.RobovmBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.compiler.AbstractCompilerMojo;
import org.apache.maven.plugin.compiler.CompilationFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;

/**
 * A modified version of maven compiler mojo.
 *
 * @author Alfio Gloria
 */
@Mojo( name = "ios-launcher", requiresDependencyResolution = ResolutionScope.COMPILE )
public class LauncherMojo extends AbstractCompilerMojo {

    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> classpathElements;

    @Parameter( defaultValue = "${project.build.directory}/generated-sources/annotations" )
    private File generatedSourcesDirectory;

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private IosConf iosConf;

    @Component
    private TemplateUtils templateUtils;

    @Component
    private RobovmBuilder robovmBuilder;

    @Override
    public void execute() throws MojoExecutionException, CompilationFailureException {
        if ( robovmBuilder.isDefLaucherUsed() ) {
            Map<String, String> root = new HashMap<>();
            root.put( "package", robovmBuilder.getDefLauncherPackage() );
            root.put( "launcherName", robovmBuilder.getDefLauncherName() );
            root.put( "mainClass", mobileConf.getMainClass() );
            root.put( "preloaderClass", mobileConf.getPreloaderClass() );

            templateUtils.process( "BasicLauncher.tpl", IosTarget.DEFLAUNCHER, root );

            super.execute();
        }
    }

    @Override
    protected SourceInclusionScanner getSourceInclusionScanner( int staleMillis ) {
        return new StaleSourceScanner( staleMillis );
    }

    @Override
    protected SourceInclusionScanner getSourceInclusionScanner( String inputFileEnding ) {
        return new SimpleSourceInclusionScanner(
                Collections.singleton( "**/*" + ( inputFileEnding.startsWith( "." ) ? "" : "." ) + inputFileEnding ),
                Collections.<String>emptySet()
        );
    }

    @Override
    protected List<String> getClasspathElements() {
        List<String> elements = new ArrayList<>();
        elements.addAll( classpathElements );

        List<Artifact> artifacts = new ArrayList<>();
        artifacts.addAll( iosConf.getUserArtifacts() );
        artifacts.addAll( iosConf.getBuildingArtifacts() );

        for ( Artifact artifact : artifacts ) {
            elements.add( artifact.getFile().toString() );
        }

        return elements;
    }

    @Override
    protected List<String> getCompileSourceRoots() {
        return Arrays.asList( fs.string( IosTarget.GENERATEDSOURCEDIR ) );
    }

    @Override
    protected File getOutputDirectory() {
        return fs.file( IosTarget.CLASSESDIR );
    }

    @Override
    protected String getSource() {
        return source;
    }

    @Override
    protected String getTarget() {
        return target;
    }

    @Override
    protected String getCompilerArgument() {
        return compilerArgument;
    }

    @Override
    protected Map<String, String> getCompilerArguments() {
        return compilerArguments;
    }

    @Override
    protected File getGeneratedSourcesDirectory() {
        return generatedSourcesDirectory;
    }
}
