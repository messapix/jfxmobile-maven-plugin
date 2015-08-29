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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.logging.Logger;
import org.robovm.compiler.AppCompiler;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.IOSSimulatorLaunchParameters;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "ios-simulator" )
public class SimultorMojo extends AbstractMojo {
    @Component
    private IosConf iosConf;
    
    @Component
    private RobovmBuilder robovmBuilder;
    
    @Component
    private Logger log;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Config config = robovmBuilder.build();
        
        DeviceType deviceType = getDeviceType();
        
        getLog().info( "ios simulator os " + config.getOs() );
        getLog().info( "ios simulator arch " + config.getArch() );
        getLog().info( "ios simulator target " + config.getTarget() );
        getLog().info( "ios simulator classpath entries " + config.getClasspath() );
        getLog().info( "ios simulator force link classes " + config.getForceLinkClasses() );
        getLog().info( "ios simulator info.plist " + iosConf.getInfoPList() );
        getLog().info( "ios simulator libs " + config.getLibs() );
        getLog().info( "ios simulator device type " + deviceType );
        
        AppCompiler compiler = new AppCompiler( config );
        
        IOSSimulatorLaunchParameters launchParameters = (IOSSimulatorLaunchParameters)config.getTarget().createLaunchParameters();
        launchParameters.setDeviceType( deviceType );
        
        try {
            compiler.build();
            compiler.launch( launchParameters );
        }
        catch ( Throwable ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }
    
    protected DeviceType getDeviceType() {
        if ( iosConf.getDeviceName() != null ) {
            return DeviceType.getBestDeviceType(
                    iosConf.getArch(),
                    iosConf.getDeviceFamily(),
                    iosConf.getDeviceName(),
                    iosConf.getSDK().getVersion()
            );
        }
        else {
            DeviceType bestDeviceType = DeviceType.getBestDeviceType();
            log.warn( "No device name is set. " + bestDeviceType.getDeviceName() + " used" );
            return bestDeviceType;
        }
    }
}
