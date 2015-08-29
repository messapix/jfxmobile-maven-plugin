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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Alfio Gloria
 */
public abstract class AbstractMobileMojo extends AbstractMojo {
    @Parameter( defaultValue = "${jfxmobile.version}" )
    private String javafxportsVersion;

    @Parameter( defaultValue = "${jfxmobile.mainClass}" )
    private String mainClass;

    @Parameter( defaultValue = "${jfxmobile.preloaderClass}" )
    private String preloaderClass;

    @Component
    private MobileConf mc;

    protected void init() throws MojoExecutionException, MojoFailureException {
        if ( !mc.isConfigured() ) {
            mc.configure( javafxportsVersion, mainClass, preloaderClass );
        }
    }
}
