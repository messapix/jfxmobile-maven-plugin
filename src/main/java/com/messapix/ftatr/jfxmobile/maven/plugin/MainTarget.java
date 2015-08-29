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
package com.messapix.ftatr.jfxmobile.maven.plugin;

/**
 *
 * @author Alfio Gloria
 */
public enum MainTarget implements Target {
    TEMPDIR( "main/tmp", Type.DIR ),
    RETROLAMBDADIR( "main/tmp/retrolambda", Type.DIR ),
    RETROLAMBDAJAR( "main/tmp/${name}-main-retrolambda.jar", Type.FILE );

    private final Type type;

    private final String relativePath;

    private MainTarget( String relativePath, Type type ) {
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

}
