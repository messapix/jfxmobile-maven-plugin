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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded.lifecycle;

import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MavenAnt;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.EmbeddedTarget;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.model.EmbeddedData;
import com.messapix.ftatr.jfxmobile.maven.plugin.embedded.model.RemotePlatform;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "embedded-run" )
public class RunMojo extends AbstractMojo {
    @Component
    private EmbeddedData data;

    @Component
    private MavenAnt ant;

    @Component
    private MobileConf mobileConf;

    @Component
    private FileSystem fs;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RemotePlatform platform = data.getRemotePlatform();

        SSHExec ssh = new SSHExec();
        ssh.setProject( ant.getAntProject() );
        ssh.setHost( platform.getHost() );
        ssh.setPort( platform.getPort() );
        ssh.setUsername( platform.getUsername() );
        ssh.setTrust( true );
        ssh.setUsePty( true );

        if ( platform.getPassword() != null ) {
            ssh.setPassword( platform.getPassword() );
        }
        else {
            ssh.setKeyfile( platform.getKeyfile().getAbsolutePath() );
            ssh.setPassphrase( platform.getPassphrase() );
        }

        ssh.setCommand( "cd " + platform.getWorkingDir() + "/" + mobileConf.getProjectName() + "; "
                + platform.getExecPrefix() + "'"
                + ( platform.getJreLocation() != null ? "/bin/" : "" )
                + "java' -Dfile.encoding=UTF-8 -jar " + fs.fileName( EmbeddedTarget.JAR ) );

        ssh.execute();
    }
}
