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
    private Boolean resourceFiltering;

    private List<Dependency> dependencies;

    @AsProperty
    private File iosSdk;

    @AsProperty
    private File robovmSdk;

    @AsProperty
    private String launcherClassName;

    private List<String> forceLinkClasses;

    @AsProperty
    private File infoPList;

    @AsProperty
    private File robovmPropertiesFile;

    @AsProperty
    private File robovmConfigFile;

    @AsProperty
    @Default( "ios" )
    private String os;

    @AsProperty
    @Default( "false" )
    private Boolean skipSigning;

    @AsProperty
    private String signIdentity;

    @AsProperty
    private String provisioningProfile;

    @AsProperty
    @Default( "false" )
    private Boolean debug;

    @AsProperty
    @Default( "-1" )
    private Integer debugPort;

    @AsProperty( separator = ":" )
    @Default( "thumbv7" )
    private List<String> ipaArchs;

    @Inspect
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

}
