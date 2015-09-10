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
package com.messapix.ftatr.jfxmobile.maven.plugin.android.standalone;

import com.messapix.ftatr.jfxmobile.maven.plugin.Executor;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AbstractAndroidMojo;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-log" )
public class LogMojo extends AbstractAndroidMojo {
    @Component
    private AndroidConf androidConf;

    @Component
    private Executor executor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Configuration for standalone mode
        if ( !androidConf.isConfigured() ) {
            super.init();
        }

        executor.exeOnScreen(
                androidConf.getPlatformTool( "adb" ),
                "logcat"
        );
    }
}
