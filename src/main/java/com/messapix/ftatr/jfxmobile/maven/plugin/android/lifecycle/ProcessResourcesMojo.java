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

import com.android.builder.core.BuilderConstants;
import com.android.ide.common.internal.PngCruncher;
import com.android.ide.common.internal.PngException;
import com.android.ide.common.res2.AssetMerger;
import com.android.ide.common.res2.AssetSet;
import com.android.ide.common.res2.MergedAssetWriter;
import com.android.ide.common.res2.MergedResourceWriter;
import com.android.ide.common.res2.MergingException;
import com.android.ide.common.res2.ResourceMerger;
import com.android.ide.common.res2.ResourceSet;
import com.messapix.ftatr.jfxmobile.maven.plugin.Executor;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidSource;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-process-resources" )
public class ProcessResourcesMojo extends AbstractMojo {
    @Component
    private AndroidConf androidConf;

    @Component
    private Executor executor;

    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        {
            AssetSet set = new AssetSet( BuilderConstants.MAIN );
            AssetMerger merger = new AssetMerger();
            MergedAssetWriter writer = new MergedAssetWriter( fs.file( AndroidTarget.ASSETSDIR ) );

            try {
                set.addSource( fs.file( AndroidSource.ASSETSDIR ) );
                set.loadFromFiles( null );
                merger.addDataSet( set );
                merger.mergeData( writer, false );
            }
            catch ( MergingException ex ) {
                throw new MojoExecutionException( "Error", ex );
            }
        }

        {
            PngCruncher cruncher = new AaptSingleCruncher( androidConf.getBuildTool( "aapt" ) );
            ResourceSet set = new ResourceSet( BuilderConstants.MAIN );
            ResourceMerger merger = new ResourceMerger();
            MergedResourceWriter writer = new MergedResourceWriter( fs.file( AndroidTarget.RESDIR ), cruncher, true, true );

            try {
                set.addSource( fs.file( AndroidSource.RESDIR ) );
                set.loadFromFiles( null );
                merger.addDataSet( set );
                merger.mergeData( writer, false );
            }
            catch ( MergingException ex ) {
                throw new MojoExecutionException( "Error", ex );
            }
        }

        executor.exe(
                androidConf.getBuildTool( "aapt" ),
                "package",
                "-f",
                "--no-crunch",
                "-I", androidConf.getAndroidJar().toString(),
                "-M", fs.string( AndroidTarget.MANIFEST ),
                "-S", fs.string( AndroidTarget.RESDIR ),
                "-A", fs.string( AndroidTarget.ASSETSDIR ),
                "-F", fs.string( AndroidTarget.RESOURCESAPK ),
                "--debug-mode",
                "-0",
                "apk"
        );

    }

    private class AaptSingleCruncher implements PngCruncher {

        private final File aaptExe;

        AaptSingleCruncher( File aaptExe ) {
            this.aaptExe = aaptExe;
        }

        void crunchPng( File from, File to ) throws PngException {

        }

        @Override
        public int start() {
            return 0;
        }

        @Override
        public void crunchPng( int key, File from, File to ) throws PngException {
            try {
                executor.exe(
                        this.aaptExe,
                        "singleCrunch",
                        "-i", from.getAbsolutePath(),
                        "-o", to.getAbsolutePath()
                );
            }
            catch ( MojoExecutionException ex ) {
                throw new PngException( ex );
            }
        }

        @Override
        public void end( int key ) throws InterruptedException {
            // DO NOTHING
        }

    }
}
