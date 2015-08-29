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
package com.messapix.ftatr.jfxmobile.maven.plugin.android.model;

import com.android.builder.signing.DefaultSigningConfig;
import com.android.ide.common.signing.CertificateInfo;
import com.android.ide.common.signing.KeystoreHelper;
import com.android.ide.common.signing.KeytoolException;
import com.android.prefs.AndroidLocation;
import com.android.utils.NullLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 * @author Alfio Gloria
 */
public class SigningConfig {
    private String name;

    private File storeFile = null;

    private String storePassword = null;

    private String keyAlias = null;

    private String keyPassword = null;

    private String storeType = KeyStore.getDefaultType();

    public static SigningConfig createDebugSigningConfig() throws MojoExecutionException {
        SigningConfig sc = new SigningConfig();
        sc.name = "debug";
        sc.storePassword = DefaultSigningConfig.DEFAULT_PASSWORD;
        sc.keyAlias = DefaultSigningConfig.DEFAULT_ALIAS;
        sc.keyPassword = DefaultSigningConfig.DEFAULT_PASSWORD;

        try {
            sc.storeFile = new File( KeystoreHelper.defaultDebugKeystoreLocation() );
        }
        catch ( AndroidLocation.AndroidLocationException ex ) {
            throw new MojoExecutionException( "Failed to get default debug keystore location", ex );
        }

        return sc;
    }

    public CertificateInfo createCertificate() throws MojoExecutionException {
        try {
            return KeystoreHelper.getCertificateInfo(
                    getStoreType(),
                    getStoreFile(),
                    getStorePassword(),
                    getKeyPassword(),
                    getKeyAlias()
            );
        }
        catch ( KeytoolException | FileNotFoundException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    public boolean createDebugStore() throws MojoExecutionException {
        try {
            return KeystoreHelper.createDebugStore(
                    getStoreType(),
                    getStoreFile(),
                    getStorePassword(),
                    getKeyPassword(),
                    getKeyAlias(),
                    NullLogger.getLogger() );
        }
        catch ( KeytoolException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    public boolean isSigningReady() {
        return storeFile != null
                && storePassword != null
                && keyAlias != null
                && keyPassword != null;
    }

    public boolean isDebugKeystore() throws MojoExecutionException {
        try {
            return KeystoreHelper.defaultDebugKeystoreLocation().equals( getStoreFile().getAbsolutePath());
        }
        catch ( AndroidLocation.AndroidLocationException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public File getStoreFile() {
        return storeFile;
    }

    public void setStoreFile( File storeFile ) {
        this.storeFile = storeFile;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public void setStorePassword( String storePassword ) {
        this.storePassword = storePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias( String keyAlias ) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword( String keyPassword ) {
        this.keyPassword = keyPassword;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType( String storeType ) {
        this.storeType = storeType;
    }
}
