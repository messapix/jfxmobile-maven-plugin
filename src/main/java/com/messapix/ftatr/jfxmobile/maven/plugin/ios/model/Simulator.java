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
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;

/**
 *
 * @author Alfio Gloria
 */
public class Simulator {
    @AsProperty
    @Descriptor(
             desc = "The name of the device to use in the iOS simulator. For instance iPhone4 or iPhone-5s"
    )
    private String deviceName;

    @AsProperty
    @Descriptor(
             desc = "The architecture of the target simulator. Allowed values ar x86 or x86_64",
             defaultValue = "The architecture of the local machine"
    )
    private String arch;

    @AsProperty
    @Descriptor(
             desc = "The iOS sdk version to use in the iOS simulator",
             defaultValue = "The highest version of installed sdks"
    )
    private String sdk;

    public String getDeviceName() {
        return deviceName;
    }

    public String getArch() {
        return arch;
    }

    public String getSdk() {
        return sdk;
    }

}
