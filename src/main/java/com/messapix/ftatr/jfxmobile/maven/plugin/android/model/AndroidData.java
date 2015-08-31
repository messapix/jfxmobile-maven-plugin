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
package com.messapix.ftatr.jfxmobile.maven.plugin.android.model;

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Default;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import java.io.File;
import java.util.List;
import org.apache.maven.model.Dependency;

/**
 *
 * @author Alfio Gloria
 */
public class AndroidData {
    @AsProperty
    @Descriptor(
             desc = "The android SDK directory path",
             defaultValue = "The ANDROID_HOME system environment variable"
    )
    private File androidSdk;

    @AsProperty
    @Descriptor(
             desc = "The directory containing the JavaFX port for dalvik",
             defaultValue = "It will be downloaded from maven central when not specified"
    )
    private File dalvikSdk;

    @AsProperty
    @Descriptor(
             desc = "The version of the Android build-tools that should be used",
             defaultValue = "The plugin will try to look for the highest non-preview version available in the Android SDK"
    )
    private String buildToolsVersion;

    @AsProperty
    @Descriptor(
             desc = "The name of the Android package, that uniquely identifies your application",
             defaultValue = "The package name of your main class name"
    )
    private String applicationPackage;

    @AsProperty
    @Descriptor(
             desc = "The location of the AndroidManifest.xml file",
             defaultValue = "If no specified a default AndroidManifest file will be created"
    )
    private File manifest;

    @AsProperty
    @Descriptor(
             desc = "The API version of the Android platform to compile against",
             defaultValue = "The highest version of installed android platforms"
    )
    private String compileSdkVersion;

    @AsProperty
    @Default( "4" )
    @Descriptor(
             desc = "The minimum API version where the application can run on"
    )
    private String minSdkVersion;

    @AsProperty
    @Default( "21" )
    @Descriptor(
             desc = "The API version that the application targets"
    )
    private String targetSdkVersion;

    @AsProperty
    private File assetsDir;

    @AsProperty
    private File resDir;

    @AsProperty
    private File nativeDir;

    @AsProperty
    @Default( "false" )
    @Descriptor(
             desc = "If true maven will resolve properties in every file from src/android/resources folder"
    )
    private boolean resourceFiltering;

    @AsProperty
    @Default( "true" )
    @Descriptor(
             desc = " If true the plugin try to apply retrolambda to every Java 8 dependencies. "
            + "Other than retrolambda it try to backport defender methods as well, "
            + "but with some [limitation](https://github.com/orfjackal/retrolambda#known-limitations)."
    )
    private boolean processJava8Dependencies;

    @Descriptor(
             desc = " A signing configuration that should be used to sign the apk. "
            + "See Signing Your Applications at the Android developer guide "
            + "for more information. The only difference with the android gradle plugin is, "
            + "that you directly set the configuration instead of a reference to a "
            + "signing configuration that was defined elsewhere",
             defaultValue = "Default debug keystore. Note that if signingConfig is defined the final "
             + "name of the APK will be *name*-release.apk otherwise it will be *name*-debug.apk"
    )
    private SigningConfig signingConfig;

    @Descriptor(
             desc = "Android APK cannot be built if there are multiple resources with the same name. "
            + "This parameter allows to exclude such resources or pick the first occurrence only"
    )
    private PackagingOptions packagingOptions;

    @Descriptor(
             desc = "Allow user to define android specific dependencies"
    )
    private List<Dependency> dependencies;

    public File getAndroidSdk() {
        return androidSdk;
    }

    public File getDalvikSdk() {
        return dalvikSdk;
    }

    public String getBuildToolsVersion() {
        return buildToolsVersion;
    }

    public String getApplicationPackage() {
        return applicationPackage;
    }

    public File getManifest() {
        return manifest;
    }

    public String getCompileSdkVersion() {
        return compileSdkVersion;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public File getAssetsDir() {
        return assetsDir;
    }

    public File getResDir() {
        return resDir;
    }

    public File getNativeDir() {
        return nativeDir;
    }

    public SigningConfig getSigningConfig() {
        return signingConfig;
    }

    public PackagingOptions getPackagingOptions() {
        return packagingOptions;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean isResourceFiltering() {
        return resourceFiltering;
    }

    public boolean isProcessJava8Dependencies() {
        return processJava8Dependencies;
    }

}
