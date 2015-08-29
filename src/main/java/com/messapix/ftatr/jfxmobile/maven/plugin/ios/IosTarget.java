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
package com.messapix.ftatr.jfxmobile.maven.plugin.ios;

import com.messapix.ftatr.jfxmobile.maven.plugin.Target;

/**
 * Target files and directory for android.
 *
 * @author Alfio Gloria
 */
public enum IosTarget implements Target {
    BASEDIR( "ios", Type.DIR ),
    TEMPDIR( "ios/tmp", Type.DIR ),
    ROBOVMDIR("ios/tmp/robovm",Type.DIR),
    RESOURCESDIR( "ios/tmp/resources", Type.DIR ),
    ASSETSDIR( "ios/tmp/assets", Type.DIR ),
    GENERATEDSOURCEDIR( "ios/tmp/gensources", Type.DIR ),
    CLASSESDIR( "ios/tmp/classes", Type.DIR ),
    DEFLAUNCHER( GENERATEDSOURCEDIR.resolve( "org/javafxports/jfxmobile/ios/BasicLauncher.java" ), Type.FILE ),
    DEFINFOPLIST( "ios/tmp/Default-Info.plist", Type.FILE );

    private final Type type;

    private final String relativePath;

    private IosTarget( String relativePath, Type type ) {
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
