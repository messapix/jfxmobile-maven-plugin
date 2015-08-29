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
package com.messapix.ftatr.jfxmobile.maven.plugin.android.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.JarMerger;
import com.messapix.ftatr.jfxmobile.maven.plugin.MainTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-singlejar", threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM )
public class MergeIntoJarMojo extends AbstractMojo {
    @Component
    private FileSystem fs;
    
    @Component
    private AndroidConf androidConf;
    
    @Component
    private MobileConf mobileConf;
    
    @Component
    private JarMerger merger;
    
    @Override
    public void execute() throws MojoExecutionException {
        merger.merge( getJars(), fs.file( AndroidTarget.SINGLEJAR ), true );
    }
    
    private Set<File> getJars() throws MojoExecutionException {
        Set<File> jars = new LinkedHashSet<>();
        jars.add( fs.file( MainTarget.RETROLAMBDAJAR ) );
        jars.add( fs.file( AndroidTarget.RETROLAMBDAJAR ) );
        
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.addAll( mobileConf.getProject().getArtifacts() );
        artifacts.addAll( androidConf.getUserArtifacts() );
        
        for ( Artifact artifact : artifacts ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) ) {
                if ( androidConf.getRetrolambdaArtifacts().containsKey( artifact ) ) {
                    jars.add( androidConf.getRetrolambdaArtifacts().get( artifact ) );
                }
                else {
                    jars.add( artifact.getFile() );
                }
            }
        }
        
        for ( Artifact artifact : androidConf.getBuildingArtifacts() ) {
            if ( "jar".equalsIgnoreCase( artifact.getType() ) && !"android.jar".equals( artifact.getFile().getName() ) ) {
                jars.add( artifact.getFile() );
            }
        }
        
        return jars;
    }
}
