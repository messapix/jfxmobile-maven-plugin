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

import com.messapix.ftatr.jfxmobile.maven.plugin.Source;

/**
 *
 * @author Alfio Gloria
 */
public enum IosSource implements Source {
    BASEDIR( "src/ios", Type.DIR ),
    RESOURCESDIR( "src/ios/resources", Type.DIR ),
    ASSETSDIR( "src/ios/assets", Type.DIR ),
    SOURCEDIR( "src/ios/java", Type.DIR ),
    NATIVEDIR( "src/ios/jniLibs", Type.DIR ),
    FRAMEWORKSDIR( "src/ios/frameworks", Type.DIR );

    private final String defaultRelativePath;

    private final Type type;

    private IosSource( String defaultRelativePath, Type type ) {
        this.defaultRelativePath = defaultRelativePath;
        this.type = type;
    }

    /**
     * @return the defaultRelativePath
     */
    @Override
    public String getDefaultRelativePath() {
        return defaultRelativePath;
    }

    /**
     * @return the type
     */
    @Override
    public Type getType() {
        return type;
    }
}
