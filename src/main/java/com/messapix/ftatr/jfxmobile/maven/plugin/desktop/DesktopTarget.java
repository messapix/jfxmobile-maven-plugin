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
package com.messapix.ftatr.jfxmobile.maven.plugin.desktop;

import com.messapix.ftatr.jfxmobile.maven.plugin.Target;

/**
 * Target files and directory for android.
 *
 * @author Alfio Gloria
 */
public enum DesktopTarget implements Target {
    // Directories
    BASEDIR( "desktop", Type.DIR ),
    TEMPDIR( "desktop/tmp", Type.DIR ),
    RESOURCESDIR( "desktop/tmp/resources", Type.DIR ),
    CLASSESDIR( "desktop/tmp/classes", Type.DIR ),
    APPDIR( "desktop/dist/app", Type.DIR ),
    APPLIBDIR( "desktop/dist/app/lib", Type.DIR ),
    UBERDIR( "desktop/dist/uber", Type.DIR ),
    // Files
    CLASSESJAR( "desktop/tmp/classes.jar", Type.FILE ),
    RESOURCESJAR( "desktop/tmp/resources.jar", Type.FILE ),
    JAR( "desktop/${name}.jar", Type.FILE ),
    UBERJAR( "desktop/dist/uber/${name}.jar", Type.FILE ),
    APPJAR( "desktop/dist/app/${name}.jar", Type.FILE );

    private final Type type;

    private final String relativePath;

    private DesktopTarget( String relativePath, Type type ) {
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
