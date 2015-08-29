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
import java.io.File;
import java.util.List;
import org.apache.maven.model.Dependency;

/**
 *
 * @author Alfio Gloria
 */
public class AndroidData {
    @AsProperty
    private File androidSdk;

    @AsProperty
    private File dalvikSdk;

    @AsProperty
    private String buildToolsVersion;

    @AsProperty
    private String applicationPackage;

    @AsProperty
    private File manifest;

    @AsProperty
    private String compileSdkVersion;

    @AsProperty
    @Default( "4" )
    private String minSdkVersion;

    @AsProperty
    @Default( "21" )
    private String targetSdkVersion;

    @AsProperty
    private File assetsDir;

    @AsProperty
    private File resDir;

    @AsProperty
    private File nativeDir;

    @AsProperty
    @Default( "false" )
    private boolean resourceFiltering;

    @AsProperty
    @Default( "true" )
    private boolean processJava8Dependencies;

    private SigningConfig signingConfig;

    private PackagingOptions packagingOptions;

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
