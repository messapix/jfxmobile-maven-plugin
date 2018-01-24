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
package com.messapix.ftatr.jfxmobile.maven.plugin.embedded.model;

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Default;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import java.io.File;

/**
 *
 * @author Alfio Gloria
 */
public class RemotePlatform {
    @AsProperty
    @Descriptor( desc = "The identifier of remote platform. It is used to instruct maven about the device to run" )
    private String id;

    @AsProperty
    @Descriptor( desc = "The ip address of the embedded device. "
            + "It must be reachable from your development environment." )
    private String host;

    @AsProperty
    @Default( "22" )
    @Descriptor( desc = "The port that is being used by the ssh server on the embedded device." )
    private int port;

    @AsProperty
    @Descriptor( desc = "The username to use when connecting to the embedded device." )
    private String username;

    @AsProperty
    @Descriptor( desc = "The password of the user that is used when connecting to the embedded device." )
    private String password;

    @AsProperty
    @Descriptor( desc = "A keyfile that is being used for connecting to the embedde device. "
            + "This property will be ignored when a password has been provided." )
    private File keyfile;

    @AsProperty
    @Descriptor( desc = "The passphrase for the configured keyfile. "
            + "This property will be ignored when a password has been provided." )
    private String passphrase;

    @AsProperty
    @Descriptor( desc = "The directory on the remote platform that will contain the project folder" )
    private String workingDir;

    @AsProperty
    @Descriptor( desc = "The installation location of JDK 8 for the embedded device." )
    private String jreLocation;

    @AsProperty
    @Descriptor( desc = "This string will be prepended to the java command, e.g. sudo" )
    private String execPrefix;

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public File getKeyfile() {
        return keyfile;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getJreLocation() {
        return jreLocation;
    }

    public String getExecPrefix() {
        return execPrefix;
    }

}
