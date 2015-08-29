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

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.Executor;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidDependencies;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.nio.file.Path;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-shrink" )
public class ShrinkMojo extends AbstractMojo {
    @Component
    private FileSystem fs;

    @Component
    private AndroidConf androidConf;

    @Component
    private Executor executor;

    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private AndroidDependencies deps;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        executor.java(
                artifactResolver.resolve( deps.proguard() ).getFile(),
                "proguard.ProGuard",
                null,
                null,
                "-injars", wrap( fs.path( AndroidTarget.SINGLEJAR ) ),
                "-outjars", wrap( fs.path( AndroidTarget.COMPONENTSJAR )),
                "-include", wrap( fs.path( AndroidTarget.MANIFESTKEEP ) ),
                "-libraryjars", wrap( androidConf.getBuildToolsLibDir().resolve( "shrinkedAndroid.jar" ) ),
                "-dontobfuscate",
                "-dontoptimize",
                "-dontpreverify",
                "-forceprocessing",
                "-dontwarn"
        );
    }

    private String wrap( Path path ) {
        return "'" + path + "'";
    }
}
