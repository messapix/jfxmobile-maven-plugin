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

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.messapix.ftatr.jfxmobile.maven.plugin.Target;
import com.messapix.ftatr.jfxmobile.maven.plugin.TemplateUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.IosTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.RobovmBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.logging.Logger;
import org.robovm.compiler.target.ios.SDK;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "ios-infoplist" )
public class InfoPlistMojo extends AbstractMojo {

    @Component
    private RobovmBuilder robovmBuilder;

    @Component
    private TemplateUtils templateUtils;

    @Component
    private IosConf iosConf;

    @Component
    private Logger log;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if ( iosConf.isDefInfoPList() ) {
            log.debug( "Create default info.plist" );
            createPlist( false, IosTarget.DEFINFOPLIST );
        }
        
        log.debug( "Create Entitlements.plist" );
        createEntitlementsPlist( IosTarget.ENTITLEMENTSPLIST );
    }

    private List<Integer> getFamilies( boolean isSimulator ) throws MojoExecutionException {
        SDK sdk = iosConf.getSDK();

        if ( sdk == null ) {
            return null;
        }

        List<Integer> families = new ArrayList<>();
        NSObject supportedDeviceFamilies = sdk.getDefaultProperty( "SUPPORTED_DEVICE_FAMILIES" );

        if ( supportedDeviceFamilies != null ) {
            // SUPPORTED_DEVICE_FAMILIES is either a NSString of comma separated numbers
            // or an NSArray with NSStrings. UIDeviceFamily values should be NSNumbers. joeri.
            if ( supportedDeviceFamilies instanceof NSString ) {
                NSString defFamilies = (NSString)supportedDeviceFamilies;

                for ( String family : defFamilies.toString().split( "," ) ) {
                    families.add( Integer.parseInt( family.trim() ) );
                }
            }
            else {
                NSArray defFamilies = (NSArray)supportedDeviceFamilies;
                for ( NSObject family : defFamilies.getArray() ) {
                    families.add( Integer.parseInt( family.toString().trim() ) );
                }
            }
        }

        return families;
    }

    private void createPlist( boolean isSimulator, Target target ) throws MojoExecutionException {
        Map<String, Object> root = new HashMap<>();
        root.put( "mainClass", robovmBuilder.getLauncher() );
        root.put( "executableName", robovmBuilder.getExecutableName() );
        root.put( "families", getFamilies( isSimulator ) );

        templateUtils.process( "infoPList.tpl", target, root );
    }
    
    private void createEntitlementsPlist( Target target ) throws MojoExecutionException {
        Map<String, Object> root = new HashMap<>();
        root.put( "apsEnvironment", iosConf.getApsEnvironment() );
    }
}
