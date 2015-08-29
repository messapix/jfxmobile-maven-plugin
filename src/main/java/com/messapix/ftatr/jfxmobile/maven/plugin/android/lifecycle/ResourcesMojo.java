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
package com.messapix.ftatr.jfxmobile.maven.plugin.android.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MojoUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidSource;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.CopyResourcesMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-resources" )
public class ResourcesMojo extends CopyResourcesMojo {
    // DO NOT REMOVE: this is to override @Parameter in CopyResourcesMojo
    @Parameter
    private File outputDirectory;

    // DO NOT REMOVE: this is to override @Parameter in CopyResourcesMojo
    @Parameter
    private List<Resource> resources;

    @Component
    private AndroidConf androidConf;

    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException {
        if ( fs.exists(AndroidSource.RESOURCESDIR ) ) {
            MojoUtils mojo = new MojoUtils( this );
            mojo.setParam( "resources", buildResources() );
            mojo.setParam( "outputDirectory", fs.file( AndroidTarget.RESOURCESDIR ) );

            super.execute();
        }
        else {
            getLog().info( "No resource folder found. Skipped." );
        }
    }

    private List<Resource> buildResources() {
        Resource resource = new Resource();
        resource.setDirectory( fs.string(AndroidSource.RESOURCESDIR ) );
        resource.setFiltering( androidConf.isResourceFiltering() );
        return Arrays.asList( resource );
    }

}
