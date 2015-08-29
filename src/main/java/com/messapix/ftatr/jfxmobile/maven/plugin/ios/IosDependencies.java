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

import com.messapix.ftatr.jfxmobile.maven.plugin.DependencyUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = IosDependencies.class )
public class IosDependencies {
    private static final String ROBOVM_VERSION = "1.6.0";

    @Requirement
    private MobileConf mobileConf;

    @Requirement
    private IosConf iosConf;

    @Requirement
    private DependencyUtils utils;

    public Dependency sdk() {
        return utils.zip( "org.javafxports", "ios-sdk", mobileConf.getJavafxportsVersion() );
    }

    public Dependency robovm() {
        Dependency dependency = new Dependency();
        dependency.setGroupId( "org.robovm" );
        dependency.setArtifactId( "robovm-dist" );
        dependency.setVersion( ROBOVM_VERSION );
        dependency.setClassifier( "nocompiler" );
        dependency.setType( "tar.gz" );
        return dependency;
    }

    public Dependency jfxrt() {
        return utils.system( "jfxrt", iosConf.getSdkLibDir().resolve( "ext/jfxrt.jar" ) );
    }

    public Dependency compat() {
        return utils.system( "compat", iosConf.getSdkLibDir().resolve( "ext/compat-1.0.0.jar" ) );
    }

    public Dependency robovmRt() {
        return utils.compile( "org.robovm", "robovm-rt", ROBOVM_VERSION );
    }

    public Dependency cocoatouch() {
        return utils.compile( "org.robovm", "robovm-cocoatouch", ROBOVM_VERSION );
    }
    
    public Dependency objc() {
        return utils.compile( "org.robovm", "robovm-objc", ROBOVM_VERSION );
    }

}
