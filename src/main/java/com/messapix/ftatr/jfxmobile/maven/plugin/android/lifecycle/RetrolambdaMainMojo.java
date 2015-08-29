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
import com.messapix.ftatr.jfxmobile.maven.plugin.MainTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.MojoUtils;
import net.orfjackal.retrolambda.maven.ProcessMainClassesMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Retrolambda main classes.
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-retrolambda-main", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE )
public class RetrolambdaMainMojo extends ProcessMainClassesMojo {

    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException {
        MojoUtils mojo = new MojoUtils( this );

        mojo.setParam( "mainInputDir", fs.targetDir().resolve( "classes" ).toFile() );
        mojo.setParam( "mainOutputDir", fs.file( MainTarget.RETROLAMBDADIR ) );

        super.execute();
    }
}
