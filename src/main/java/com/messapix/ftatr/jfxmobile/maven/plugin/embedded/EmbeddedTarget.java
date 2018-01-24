/*
 * Copyright 2016 Messapix.
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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded;

import com.messapix.ftatr.jfxmobile.maven.plugin.Target;

/**
 * Target files and directory for android.
 *
 * @author Alfio Gloria
 */
public enum EmbeddedTarget implements Target {
    BASEDIR( "embedded", Type.DIR ),
    TEMPDIR( "embedded/tmp", Type.DIR ),
    RESOURCESDIR( "embedded/tmp/resources", Type.DIR ),
    CLASSESDIR( "embedded/tmp/classes", Type.DIR ),
    DISTDIR( "embedded/dist", Type.DIR ),
    // Files
    CLASSESJAR( "embedded/tmp/classes.jar", Type.FILE ),
    RESOURCESJAR( "embedded/tmp/resources.jar", Type.FILE ),
    JAR( "embedded/dist/${name}.jar", Type.FILE );

    private final Type type;

    private final String relativePath;

    private EmbeddedTarget( String relativePath, Type type ) {
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
