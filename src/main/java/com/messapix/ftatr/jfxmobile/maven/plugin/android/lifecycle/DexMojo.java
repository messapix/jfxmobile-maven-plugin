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

import com.google.common.io.Files;
import com.messapix.ftatr.jfxmobile.maven.plugin.Executor;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-dex" )
public class DexMojo extends AbstractMojo {

    @Parameter( defaultValue = "2g" )
    private String javaMaxHeapSize;

    @Parameter( defaultValue = "false" )
    private boolean jumboMode;

    @Component
    private FileSystem fs;

    @Component
    private AndroidConf androidConf;

    @Component
    private Executor executor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Files.write( fs.string( AndroidTarget.SINGLEJAR ), fs.file( AndroidTarget.INPUTLIST ), Charset.defaultCharset() );
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Cannot create input-list file" );
        }

        executor.java( androidConf.getBuildToolsLibDir().resolve( "dx.jar" ).toFile(),
                       "com.android.dx.command.Main",
                       null,
                       "-Xmx" + javaMaxHeapSize,
                       "--dex",
                       "--verbose",
                       jumboMode ? "--force-jumbo" : null,
                       "--no-optimize",
                       "--multi-dex",
                       "--main-dex-list=" + fs.path( AndroidTarget.MAINDEXLIST ),
                       "--core-library",
                       "--output=" + fs.path( AndroidTarget.DEXDIR ),
                       "--input-list=" + fs.path( AndroidTarget.INPUTLIST )
        );
    }
}
