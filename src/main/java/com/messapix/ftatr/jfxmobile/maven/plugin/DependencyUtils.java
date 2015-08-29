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

import java.nio.file.Path;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = DependencyUtils.class )
public class DependencyUtils {
    @Requirement
    private MobileConf mobileConf;

    public Dependency system( String artifactId, Path path ) {
        Dependency dependency = new Dependency();
        dependency.setGroupId( "com.messapix.ftatr.jfxmobile.system" );
        dependency.setArtifactId( artifactId );
        dependency.setVersion( mobileConf.getJavafxportsVersion() );
        dependency.setScope( Artifact.SCOPE_SYSTEM );
        dependency.setType( "jar" );
        dependency.setSystemPath( path.toString() );
        return dependency;
    }

    public Dependency compile( String groupId, String artifactId, String version ) {
        Dependency dependency = new Dependency();
        dependency.setGroupId( groupId );
        dependency.setArtifactId( artifactId );
        dependency.setVersion( version );
        dependency.setScope( Artifact.SCOPE_COMPILE );
        dependency.setType( "jar" );
        return dependency;
    }

    public Dependency zip( String groupId, String artifactId, String version ) {
        Dependency dependency = compile( groupId, artifactId, version );
        dependency.setType( "zip" );
        return dependency;
    }
}
