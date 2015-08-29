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
package com.messapix.ftatr.jfxmobile.maven.plugin.desktop;

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.ModelInitializator;
import com.messapix.ftatr.jfxmobile.maven.plugin.desktop.model.DesktopData;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = DesktopConf.class )
public class DesktopConf {
    private boolean filtering = false;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private ModelInitializator modelInitializator;

    @Requirement
    private Logger log;

    private final List<Artifact> userArtifacts = new ArrayList<>();

    public void configure( DesktopData desktopData ) throws MojoExecutionException {
        log.debug( "Configuring build for Desktop" );

        modelInitializator.process( desktopData, "jfxmobile.desktop" );

        this.filtering = desktopData.getResourceFiltering();

        if ( desktopData.getDependencies() != null ) {
            for ( Dependency dependency : desktopData.getDependencies() ) {
                userArtifacts.add( artifactResolver.resolve( dependency ) );
            }
        }
    }

    public boolean isFiltering() {
        return filtering;
    }

    public List<Artifact> getUserArtifacts() {
        return userArtifacts;
    }
}
