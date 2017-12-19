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
import org.robovm.compiler.target.ios.IOSDeviceLaunchParameters;
import org.robovm.libimobiledevice.IDevice;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "ios-device" )
public class DeviceMojo extends AbstractMojo {
    @Component
    private IosConf iosConf;

    @Component
    private RobovmBuilder robovmBuilder;

    @Component
    private Logger log;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Config config = robovmBuilder.build();

        if ( log.isDebugEnabled() ) {
            String[] udids = IDevice.listUdids();

            log.debug( "Connected devices: " );
            if ( udids != null && udids.length != 0 ) {
                for ( String udid : udids ) {
                    log.debug( "Device: " + udid );
                }
            }
            else {
                log.debug( "No device found" );
            }
        }

        getLog().info( "ios device os " + config.getOs() );
        getLog().info( "ios device arch " + config.getArch() );
        getLog().info( "ios device target " + config.getTarget() );
        getLog().info( "ios device classpath entries " + config.getClasspath() );
        getLog().info( "ios device force link classes " + config.getForceLinkClasses() );
        getLog().info( "ios device info.plist " + iosConf.getInfoPList() );
        getLog().info( "ios device libs " + config.getLibs() );

        AppCompiler compiler = new AppCompiler( config );

        IOSDeviceLaunchParameters launchParameters = (IOSDeviceLaunchParameters)config.getTarget().createLaunchParameters();

        if ( iosConf.getDeviceId() != null ) {
            launchParameters.setDeviceId( iosConf.getDeviceId() );
            log.info( "ios device udid: " + launchParameters.getDeviceId() );
        }
        else {
            String[] udids = IDevice.listUdids();
            log.info( "ios device udid: " + ( udids != null ? udids[0] : "no device" ) );
        }

        try {
            compiler.build();
            compiler.launch( launchParameters );
        }
        catch ( Throwable ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }
}
