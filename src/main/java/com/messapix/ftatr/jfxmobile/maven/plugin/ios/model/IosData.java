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
package com.messapix.ftatr.jfxmobile.maven.plugin.ios.model;

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Default;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Inspect;
import java.io.File;
import java.util.List;
import org.apache.maven.model.Dependency;

/**
 *
 * @author Alfio Gloria
 */
public class IosData {
    @AsProperty
    @Default( "false" )
    @Descriptor(
             desc = "If true maven will resolve properties in every file from src/ios/resources folder"
    )
    private Boolean resourceFiltering;

    @Descriptor(
             desc = "Allow user to define iOS specific dependencies"
    )
    private List<Dependency> dependencies;

    @AsProperty
    @Descriptor(
             desc = "The directory containing the JavaFX port for ios",
             defaultValue = "It will be downloaded from maven central when not specified"
    )
    private File iosSdk;

    @AsProperty
    private File robovmSdk;

    @AsProperty
    @Descriptor(
             desc = "Used to define a custom laucher class for robovm",
             defaultValue = "The plugin generates a default launcher"
    )
    private String launcherClassName;

    @Descriptor(
             desc = "A list of string patterns for classes and/or complete packages that should be "
            + "linked when starting the RoboVM compiler. "
            + "See http://docs.robovm.com/configuration.html#-lt-forcelinkclasses-gt for "
            + "more information about which patterns can be used."
    )
    private List<String> forceLinkClasses;

    @AsProperty
    @Descriptor( desc = "Custom Info.plist file to use",
                 defaultValue = "The plugin will generate a default Default-Info.plist file. "
                 + "A copy can be found in build/javafxports/ios/tmp"
    )
    private File infoPList;

    @AsProperty
    @Descriptor(
             desc = "The path to a RoboVM properties file which contains info for the application",
             defaultValue = "The plugin will try to search for robovm.properties and robovm.local.properties in src/ios folder"
    )
    private File robovmPropertiesFile;

    @AsProperty
    @Descriptor(
             desc = "The path to a RoboVM configuration file which configures the RoboVM compiler",
             defaultValue = "The plugin will try to search for robovm.xml in src/ios folder"
    )
    private File robovmConfigFile;

    @AsProperty
    @Default( "ios" )
    @Descriptor(
             desc = "The operating system to use when running the application. Can be any of the following values: ios, macosx, linux"
    )
    private String os;

    @AsProperty
    @Default( "false" )
    @Descriptor(
             desc = "A boolean specifying whether signing of the application should be skipped or not"
    )
    private Boolean skipSigning;

    @AsProperty
    @Descriptor(
             desc = "The name of the identity to sign with when building an iOS bundle for the application",
             defaultValue = "Default is to look for an identity starting with ‘iPhone Developer’ or ‘iOS Development’"
    )
    private String signIdentity;

    @AsProperty
    @Descriptor(
             desc = "The name of the provisioning profile to use when building for device"
    )
    private String provisioningProfile;

    @AsProperty
    @Default( "false" )
    @Descriptor(
             desc = "A boolean specifying whether the application should be launched in debug mode or not. "
            + "The application will suspend before the main method is called and will wait for a debugger to connect"
    )
    private Boolean debug;

    @AsProperty
    @Default( "-1" )
    @Descriptor(
             desc = "An integer specifying the port to listen for debugger connections on when launching in debug mode",
             defaultValue = "If not set a default port will be used. The port actually used "
             + "will be written to the console before the app is launched"
    )
    private Integer debugPort;

    @AsProperty( separator = ":" )
    @Default( "thumbv7" )
    @Descriptor(
             desc = "A list of architectures to include in the IPA. Either thumbv7 or arm64 or both."
            + "If used in property archs must be separated by a colon. For example jfxmobile.ios.ipaArchs=thumbv7:arm64"
    )
    private List<String> ipaArchs;

    @AsProperty
    @Descriptor( desc = "The identifier of the target device" )
    private String deviceId;

    @Inspect
    @Descriptor(
             desc = "This element contains simulator settings. See table below"
    )
    private Simulator simulator;

    public Boolean getResourceFiltering() {
        return resourceFiltering;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public File getIosSdk() {
        return iosSdk;
    }

    public File getRobovmSdk() {
        return robovmSdk;
    }

    public String getLauncherClassName() {
        return launcherClassName;
    }

    public List<String> getForceLinkClasses() {
        return forceLinkClasses;
    }

    public File getInfoPList() {
        return infoPList;
    }

    public File getRobovmPropertiesFile() {
        return robovmPropertiesFile;
    }

    public File getRobovmConfigFile() {
        return robovmConfigFile;
    }

    public String getOs() {
        return os;
    }

    public Boolean getSkipSigning() {
        return skipSigning;
    }

    public String getSignIdentity() {
        return signIdentity;
    }

    public String getProvisioningProfile() {
        return provisioningProfile;
    }

    public Boolean getDebug() {
        return debug;
    }

    public Integer getDebugPort() {
        return debugPort;
    }

    public List<String> getIpaArchs() {
        return ipaArchs;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
