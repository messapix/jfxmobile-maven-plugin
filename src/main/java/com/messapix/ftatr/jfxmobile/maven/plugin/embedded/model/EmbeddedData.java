/*
 * Copyright 2016 Messapix.
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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded.model;

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Default;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Inspect;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 * @author Alfio Gloria
 */
public class EmbeddedData {
    @AsProperty
    @Default( "false" )
    @Descriptor(
             desc = "If true maven will resolve properties in every file from src/embedded/resources folder"
    )
    private Boolean resourceFiltering;

    @Descriptor(
             desc = "Allow user to define embedded specific dependencies"
    )
    private List<Dependency> dependencies;

    @Inspect
    @Descriptor( desc = "List of remote platforms" )
    private List<RemotePlatform> remotePlatforms;

    @AsProperty
    @Descriptor( desc = "The remote platform id that will be used. It is mostly used as property passed at runtime" )
    private String deviceId;

    public Boolean getResourceFiltering() {
        return resourceFiltering;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<RemotePlatform> getRemotePlatforms() {
        return remotePlatforms;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public RemotePlatform getRemotePlatform() throws MojoExecutionException {
        if ( remotePlatforms == null || remotePlatforms.isEmpty() ) {
            throw new MojoExecutionException( "No romote platform defined." );
        }

        if ( getDeviceId() == null ) {
            if ( remotePlatforms.size() == 1 ) {
                return remotePlatforms.get( 0 );
            }
            else {
                throw new MojoExecutionException( "Multiple remote platforms defined. Please use jfxmobile.embedded.deviceId to chose wich platform to deploy or run." );
            }
        }

        for ( RemotePlatform remotePlatform : remotePlatforms ) {
            if ( getDeviceId().trim().equals( remotePlatform.getId() ) ) {
                return remotePlatform;
            }
        }

        throw new MojoExecutionException( "No '" + getDeviceId() + "' remote platform was found" );
    }

}
