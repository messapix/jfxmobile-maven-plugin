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
package com.messapix.ftatr.jfxmobile.maven.plugin.ios.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.RobovmBuilder;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.robovm.compiler.AppCompiler;
import org.robovm.compiler.config.Config;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "ios-ipa" )
public class CreateIPAMojo extends AbstractMojo {
    @Component
    private RobovmBuilder robovmBuilder;

    @Component
    private IosConf iosConf;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Config config = robovmBuilder.build();

        getLog().info( "ipa os " + config.getOs() );
        getLog().info( "ipa target " + config.getTarget() );
        getLog().info( "ipa archs " + iosConf.getIpaArchs() );
        getLog().info( "ipa classpath entries " + config.getClasspath() );
        getLog().info( "ipa force link classes " + config.getForceLinkClasses() );
        getLog().info( "ipa info.plist " + iosConf.getInfoPList() );
        getLog().info( "ipa libs " + config.getLibs() );

        AppCompiler compiler = new AppCompiler( config );
        
        try {
            compiler.build();
            compiler.archive();
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

}
