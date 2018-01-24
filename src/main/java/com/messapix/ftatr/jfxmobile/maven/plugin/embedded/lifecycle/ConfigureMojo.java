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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.Target;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.AbstractEmbeddedMojo;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.EmbeddedTarget;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "embedded-configure" )
public class ConfigureMojo extends AbstractEmbeddedMojo {
    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.init();

        // Create target dirs
        for ( Target target : EmbeddedTarget.values() ) {
            if ( target.getType() == Target.Type.DIR ) {
                fs.file( target ).mkdirs();
            }
        }
    }
}
