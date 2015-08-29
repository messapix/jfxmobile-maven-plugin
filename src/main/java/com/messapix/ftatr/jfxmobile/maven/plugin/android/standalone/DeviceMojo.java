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
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AbstractAndroidMojo;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-device" )
public class DeviceMojo extends AbstractAndroidMojo {
    @Component
    private AndroidConf androidConf;

    @Component
    private FileSystem fs;

    @Component
    private Executor executor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Configuration for standalone mode
        if ( !androidConf.isConfigured() ) {
            super.init();
        }

        String output = executor.exe( 
                androidConf.getPlatformTool( "adb" ), 
                "install", 
                fs.string( androidConf.isDebugMode() ? AndroidTarget.DEBUGAPK : AndroidTarget.APK )
        );

        // FIXME: Due to issue https://code.google.com/p/android/issues/detail?id=3254
        // adb always returns 0 even if errors occour. A temporary solution is to inspect output
        if ( output.contains( "Failure" ) ) {
            getLog().error( output );
            throw new MojoExecutionException( "Cannot install apk" );
        }
    }
}
