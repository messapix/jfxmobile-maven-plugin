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

import com.messapix.ftatr.jfxmobile.maven.plugin.Target;

/**
 * Target files and directory for android.
 *
 * @author Alfio Gloria
 */
public enum AndroidTarget implements Target {
    // Directories
    BASEDIR( "android", Type.DIR ),
    TEMPDIR( "android/tmp", Type.DIR ),
    MULTIDEXDIR( "android/tmp/multi-dex", Type.DIR ),
    DEXDIR( "android/tmp/dex", Type.DIR ),
    ASSETSDIR( "android/tmp/resources/assets", Type.DIR ),
    RESDIR( "android/tmp/resources/res", Type.DIR ),
    RESOURCESDIR( "android/tmp/resources/resources", Type.DIR ),
    CLASSESDIR( "android/tmp/classes", Type.DIR ),
    UNPACKDIR( "android/tmp/unpack", Type.DIR ),
    // Files
    MANIFEST( TEMPDIR.resolve( "AndroidManifest.xml" ), Type.FILE ),
    RETROLAMBDAJAR( TEMPDIR.resolve( "${name}-android-retrolambda.jar" ), Type.FILE ),
    RESOURCESJAR( TEMPDIR.resolve( "${name}-android-resources.jar" ), Type.FILE ),
    SINGLEJAR( MULTIDEXDIR.resolve( "allclasses.jar" ), Type.FILE ),
    COMPONENTSJAR( MULTIDEXDIR.resolve( "componentClasses.jar" ), Type.FILE ),
    MAINDEXLIST( MULTIDEXDIR.resolve( "maindexlist.txt" ), Type.FILE ),
    MANIFESTKEEP( MULTIDEXDIR.resolve( "manifest_keep.txt" ), Type.FILE ),
    INPUTLIST( DEXDIR.resolve( "inputList.txt" ), Type.FILE ),
    RESOURCESAPK( "android/tmp/resources/resources.ap_", Type.FILE ),
    APK( "android/${name}.apk", Type.FILE ),
    DEBUGAPK( "android/${name}-debug.apk", Type.FILE ),
    UNALIGNEDAPK( "android/${name}-unaligned.apk", Type.FILE );

    private final Type type;

    private final String relativePath;

    private AndroidTarget( String relativePath, Type type ) {
        this.type = type;
        this.relativePath = relativePath;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    private String resolve( String filename ) {
        return this.relativePath + "/" + filename;
    }
}
