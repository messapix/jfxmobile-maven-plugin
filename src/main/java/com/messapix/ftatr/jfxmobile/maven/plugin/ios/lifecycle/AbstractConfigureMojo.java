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
import com.messapix.ftatr.jfxmobile.maven.plugin.Target;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.AbstractIosMojo;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosDependencies;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.RobovmBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;

/**
 *
 * @author Alfio Gloria
 */
public abstract class AbstractConfigureMojo extends AbstractIosMojo {
    @Component
    private FileSystem fs;

    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private MobileConf mobileConf;

    @Component
    private IosConf iosConf;

    @Component
    private IosDependencies deps;

    @Component
    private RobovmBuilder robovmConfigurator;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.init();

        // Create target dirs
        for ( Target target : IosTarget.values() ) {
            if ( target.getType() == Target.Type.DIR ) {
                fs.file( target ).mkdirs();
            }
        }

        List<Artifact> artifacts = new ArrayList<>();
        artifacts.addAll( iosConf.getUserArtifacts() );
        artifacts.addAll( iosConf.getBuildingArtifacts() );
        artifacts.addAll( mobileConf.getProject().getArtifacts() );
        robovmConfigurator.classpath( artifacts );
    }
}
