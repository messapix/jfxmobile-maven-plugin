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

import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.OS;
import org.robovm.compiler.config.Resource;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = RobovmBuilder.class )
public class RobovmBuilder {
    @Requirement
    private FileSystem fs;

    @Requirement
    private MobileConf mobileConf;

    @Requirement
    private IosConf iosConf;

    @Requirement
    private Logger log;

    private final Config.Builder builder;

    private boolean defLaucherUsed = true;

    private String defLauncherName;

    private String defLauncherPackage;

    private String launcher;

    private String executableName;

    public RobovmBuilder() throws MojoExecutionException {
        try {
            builder = new Config.Builder();
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    public RobovmBuilder init() {
        executableName = mobileConf.getProjectName();

        builder.logger( createLogger() );

        builder.tmpDir( fs.file( IosTarget.ROBOVMDIR ) )
                .installDir( fs.file( IosTarget.BASEDIR ) )
                .executableName( getExecutableName() );

        addLibs(
                "libglass.a",
                "libjavafx_font.a",
                "libjavafx_iio.a",
                "libjavafx_ios_webnode.a",
                "libprism_common.a",
                "libprism_es2.a"
        );

        builder.addForceLinkClass( "javafx.**.*" )
                .addForceLinkClass( "com.sun.**.*" )
                .addForceLinkClass( "com.android.**.*" )
                .addForceLinkClass( "org.apache.harmony.security.provider.**.*" )
                .addForceLinkClass( "sun.util.logging.PlatformLogger" )
                .addForceLinkClass( "java.util.logging.**.*" );

        builder.addFramework( "UIKit" )
                .addFramework( "OpenGLES" )
                .addFramework( "QuartzCore" )
                .addFramework( "CoreGraphics" )
                .addFramework( "CoreText" )
                .addFramework( "ImageIO" )
                .addFramework( "MobileCoreServices" )
                .addFramework( "CoreMedia" )
                .addFramework( "AVFoundation" )
                .addFramework( "MediaPlayer" );

        if ( fs.exists( IosSource.ASSETSDIR ) ) {
            builder.addResource( new Resource( fs.file( IosSource.ASSETSDIR ) ) );
        }

        builder.targetType( "ios" );

        return this;
    }

    public void forIpa( OS os, List<Arch> ipaArchs ) {
        builder.skipInstall( false );
        builder.os( os );
        builder.archs( ipaArchs );
    }

    public void forSimulator( Arch arch ) {
        builder.skipInstall( true );
        builder.os( OS.ios );
        builder.arch( arch );
    }

    public void forDevice( Arch arch ) {
        builder.skipInstall( true );
        builder.os( OS.ios );
        builder.arch( arch );
    }

    public RobovmBuilder files( File configFile, File propertiesFile ) throws MojoExecutionException {
        if ( configFile != null ) {
            if ( !configFile.exists() ) {
                throw new MojoExecutionException( "The configured robovm config file " + configFile + " cannot be found" );
            }

            log.debug( "Loading config file for RoboVM compiler: " + configFile );
            try {
                builder.read( configFile );
            }
            catch ( IOException ex ) {
                throw new MojoExecutionException( "Failed to read RoboVM config file: " + configFile );
            }
        }
        else {
            try {
                builder.readProjectConfig( fs.file( IosSource.BASEDIR ), false );
            }
            catch ( IOException ex ) {
                throw new MojoExecutionException( "Failed to read RoboVM config file in " + fs.string( IosSource.BASEDIR ) );
            }
        }

        if ( propertiesFile != null ) {
            if ( !propertiesFile.exists() ) {
                throw new MojoExecutionException( "The configured robovm properties file " + propertiesFile + " cannot be found" );
            }

            log.debug( "Including properties file in RoboVM compiler config: " + propertiesFile );

            try {
                builder.addProperties( propertiesFile );
            }
            catch ( IOException ex ) {
                throw new MojoExecutionException( "Failed to add properties file to RoboVM config: " + propertiesFile );
            }
        }
        else {
            try {
                builder.readProjectProperties( fs.file( IosSource.BASEDIR ), false );
            }
            catch ( IOException ex ) {
                throw new MojoExecutionException( "Failed to read RoboVM project properties file(s) in " + fs.string( IosSource.BASEDIR ) );
            }
        }

        return this;
    }

    public RobovmBuilder home( Path path ) {
        builder.home( new Config.Home( path.toFile() ) );
        return this;
    }

    public RobovmBuilder launcher( String className ) {
        if ( className == null || className.trim().isEmpty() ) {
            defLauncherPackage = fs.path( IosTarget.GENERATEDSOURCEDIR )
                    .relativize( fs.path( IosTarget.DEFLAUNCHER ).getParent() )
                    .toString()
                    .replace( "/", "." );

            defLauncherName = fs.fileNameWithoutExt( IosTarget.DEFLAUNCHER );
            launcher = getDefLauncherPackage() + "." + getDefLauncherName();
            builder.mainClass( getDefLauncherPackage() + "." + getDefLauncherName() );
            defLaucherUsed = true;
        }
        else {
            builder.mainClass( className );
            defLaucherUsed = false;
            launcher = className;
        }

        return this;
    }

    public RobovmBuilder debug( boolean debug, int port ) {
        if ( debug ) {
            builder.debug( true );

            if ( port != -1 ) {
                builder.addPluginArgument( "debug:jdwpport=" + port );
            }
        }

        return this;
    }

    public RobovmBuilder signing( String signingIdentity, String provisioningProfile, boolean skip ) {
        log.debug( "Robovm: signing config" );

        if ( log.isDebugEnabled() ) {
            log.debug( "List of signing identities" );

            List<SigningIdentity> identities = SigningIdentity.list();
            if ( identities == null || identities.isEmpty() ) {
                log.debug( "No signing identity found" );
            }
            else {
                for ( SigningIdentity identity : identities ) {
                    log.debug( identity.toString() );
                }
            }

            log.debug( "List of provisioning profiles" );
            List<ProvisioningProfile> profiles = ProvisioningProfile.list();
            if ( profiles != null && !profiles.isEmpty() ) {
                for ( ProvisioningProfile profile : profiles ) {
                    log.debug( profile.toString() );
                }
            }
            else {
                log.debug( "No provisioning profile found" );
            }
        }

        if ( skip ) {
            builder.iosSkipSigning( true );
        }
        else {
            if ( signingIdentity != null ) {
                log.debug( "Using explicit iOS Signing identity: " + signingIdentity );
                builder.iosSignIdentity( SigningIdentity.find( SigningIdentity.list(), signingIdentity ) );
            }
            else{
                log.info( "No signing indentity provided");
            }

            if ( provisioningProfile != null ) {
                log.debug( "Using explicit iOS provisioning profile: " + provisioningProfile );
                builder.iosProvisioningProfile( ProvisioningProfile.find( ProvisioningProfile.list(), provisioningProfile ) );
            }
            else{
                log.info( "No provisioning profile provided");
            }
        }

        return this;
    }

    public RobovmBuilder classpath( List<Artifact> artifacts ) {
        builder.clearClasspathEntries();
        for ( Artifact artifact : artifacts ) {
            builder.addClasspathEntry( artifact.getFile() );
        }

        // add ios-sdk/rt/lib to classpath to include properties and .a libraries
        builder.addClasspathEntry( iosConf.getSdkLibDir().toFile() );
        builder.addClasspathEntry( new File( mobileConf.getProject().getBuild().getOutputDirectory() ) );
        builder.addClasspathEntry( fs.file( IosTarget.CLASSESDIR ) );
        builder.addClasspathEntry( fs.file( IosTarget.RESOURCESDIR ) );

        // TODO: add resource dirs to classpath
        return this;
    }

    public RobovmBuilder resource() {
        builder.addResource( new Resource( fs.file( IosTarget.ASSETSDIR ), "" ) );
        return this;
    }

    public RobovmBuilder forceLinkClasses( List<String> patterns ) {
        if ( patterns != null ) {
            for ( String pattern : patterns ) {
                builder.addForceLinkClass( pattern );
            }
        }

        return this;
    }

    public RobovmBuilder infoPList( File infoPList ) {
        if ( infoPList != null ) {
            builder.iosInfoPList( infoPList );
        }

        return this;
    }

    public Config build() throws MojoExecutionException {
        try {
            return builder.build();
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    private void addLibs( String... names ) {
        for ( String name : names ) {
            builder.addLib( new Config.Lib( iosConf.getSdkLibDir().resolve( name ).toAbsolutePath().toString(), true ) );
        }
    }

    public boolean isDefLaucherUsed() {
        return defLaucherUsed;
    }

    public String getDefLauncherName() {
        return defLauncherName;
    }

    public String getDefLauncherPackage() {
        return defLauncherPackage;
    }

    public String getExecutableName() {
        return executableName;
    }

    public String getLauncher() {
        return launcher;
    }

    private org.robovm.compiler.log.Logger createLogger() {
        return new org.robovm.compiler.log.Logger() {

            @Override
            public void debug( String format, Object... args ) {
                log.debug( String.format( format, args ) );
            }

            @Override
            public void info( String format, Object... args ) {
                log.info( String.format( format, args ) );
            }

            @Override
            public void warn( String format, Object... args ) {
                log.warn( String.format( format, args ) );
            }

            @Override
            public void error( String format, Object... args ) {
                log.error( String.format( format, args ) );
            }
        };
    }
}
