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
package com.messapix.ftatr.jfxmobile.maven.plugin.android;

import com.messapix.ftatr.jfxmobile.maven.plugin.DependencyUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = AndroidDependencies.class )
public class AndroidDependencies {

    @Requirement
    private MobileConf mobileConf;

    @Requirement
    private AndroidConf androidConf;

    @Requirement
    private DependencyUtils utils;

    public Dependency android() {
        return utils.system( "android", androidConf.getAndroidJar() );
    }

    public Dependency jfxDvk() {
        return utils.compile( "org.javafxports", "jfxdvk", mobileConf.getJavafxportsVersion() );
    }

    public Dependency dalvikSdk() {
        return utils.zip( "org.javafxports", "dalvik-sdk", mobileConf.getJavafxportsVersion() );
    }

    public Dependency jfxrt() {
        return utils.system( "jfxrt", androidConf.getDalvikSdkLibDir().resolve( "ext/jfxrt.jar" ) );
    }

    public Dependency compat() {
        return utils.system( "compat", androidConf.getDalvikSdkLibDir().resolve( "ext/compat-1.0.0.jar" ) );
    }

    public Dependency multidex() {
        return utils.system(
                "android-multidex",
                androidConf.getAndroidSdkDir().resolve( "extras/android/support/multidex/library/libs/android-support-multidex.jar" )
        );
    }

    public Dependency proguard() {
        return utils.compile( "net.sf.proguard", "proguard-base", "5.2.1" );
    }

}
