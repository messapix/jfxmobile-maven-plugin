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
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.TemplateUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidSource;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-manifest" )
public class ManifestMojo extends AbstractMojo {

    @Component
    private AndroidConf androidConf;

    @Component
    private FileSystem fs;

    @Component
    private MobileConf mobileConf;

    @Component
    private TemplateUtils templateUtils;

    @Override
    @SuppressWarnings( "UseSpecificCatch" )
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if ( fs.path( AndroidSource.MANIFEST ) != null ) {
                Files.copy( fs.path( AndroidSource.MANIFEST ), fs.path( AndroidTarget.MANIFEST ) );
            }
            else {
                Map<String, String> root = new HashMap<>();
                root.put( "packageName", androidConf.getApplicationPackage() );
                root.put( "version", mobileConf.getProjectVersion() );
                root.put( "minSdkVersion", androidConf.getMinSdkVersion() );
                root.put( "targetSdkVersion", androidConf.getTargetSdkVersion() );
                root.put( "projectName", mobileConf.getProjectName() );
                root.put( "mainClass", mobileConf.getMainClass() );
                root.put( "preloaderClass", mobileConf.getPreloaderClass() );

                templateUtils.process( "manifestTemplate.tpl", AndroidTarget.MANIFEST, root );
            }
        }
        catch ( Exception ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }
}
