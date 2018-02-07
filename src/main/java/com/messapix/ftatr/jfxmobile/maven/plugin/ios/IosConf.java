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

import com.messapix.ftatr.jfxmobile.maven.plugin.ArtifactResolver;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.ModelInitializator;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.model.IosData;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.OS;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.SDK;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = IosConf.class )
public class IosConf {
    private boolean filtering = false;

    private Path sdkDir;

    private Path sdkLibDir;

    private Path robovmDir;

    private String launcherClassName;

    private Arch arch;

    private SDK sdk;

    private String deviceName;

    private DeviceType.DeviceFamily deviceFamily;

    private OS os;

    private final List<Arch> ipaArchs = new ArrayList<>();

    private File infoPList;

    private boolean defInfoPList = false;

    private final List<Artifact> userArtifacts = new ArrayList<>();

    private final List<Artifact> buildingArtifacts = new ArrayList<>();

    private IosData iosData;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private IosDependencies deps;

    @Requirement
    private RobovmBuilder robovmBuilder;

    @Requirement
    private FileSystem fs;

    @Requirement
    private ModelInitializator modelInitializator;

    @Requirement
    private Logger log;

    public void configure( IosData iosData, Target target ) throws MojoExecutionException {
        log.debug( "Configuring build for iOS" );

        this.iosData = iosData;

        modelInitializator.process( iosData, "jfxmobile.ios" );

        this.filtering = iosData.getResourceFiltering();

        checkApsEnvironment( iosData.getApsEnvironment() );

        configureSdk( iosData.getIosSdk() );
        configureRobovm( iosData.getRobovmSdk() );

        configureInfoPList( iosData );

        switch ( target ) {
            case IPA:
                configureIpaArchs( iosData.getIpaArchs() );
                configureOS( iosData );
                break;
            case SIMULATOR:
                configureSimulatorArch( iosData.getSimulator().getArch() );
                configureTargetSDK( iosData.getSimulator().getSdk(), true );
                configureSimulatorDevice( iosData.getSimulator().getDeviceName() );
                break;
            case DEVICE:
                configureDeviceArch( null );
                configureTargetSDK( null, false );
                break;
        }

        robovmBuilder
                .init()
                .files( iosData.getRobovmConfigFile(), iosData.getRobovmPropertiesFile() )
                .home( robovmDir )
                .debug( iosData.getDebug(), iosData.getDebugPort() )
                .signing( iosData.getSignIdentity(), iosData.getProvisioningProfile(), iosData.getSkipSigning() )
                .launcher( launcherClassName )
                .resource()
                .infoPList( iosData.getInfoPList() )
                .forceLinkClasses( iosData.getForceLinkClasses() );

        switch ( target ) {
            case IPA:
                robovmBuilder.forIpa( os, ipaArchs );
                break;
            case SIMULATOR:
                robovmBuilder.forSimulator( arch );
                break;
            case DEVICE:
                robovmBuilder.forDevice( arch );
                break;
        }

        configureDependencies( iosData.getDependencies() );
    }

    public Path getSdkLibDir() {
        return sdkLibDir;
    }

    public boolean isFiltering() {
        return filtering;
    }

    public Arch getArch() {
        return arch;
    }

    public SDK getSDK() {
        return sdk;
    }

    public List<Artifact> getUserArtifacts() {
        return userArtifacts;
    }

    public List<Artifact> getBuildingArtifacts() {
        return buildingArtifacts;
    }

    private void configureInfoPList( IosData iosData ) throws MojoExecutionException {
        this.infoPList = iosData.getInfoPList();
        if ( this.getInfoPList() != null ) {
            if ( !this.infoPList.exists() ) {
                throw new MojoExecutionException( "Provided InfoPList file " + this.getInfoPList() + " does not exists" );
            }
        }
        else {
            this.infoPList = fs.file( IosTarget.DEFINFOPLIST );
            this.defInfoPList = true;
        }
    }

    private void configureSdk( File sdkParam ) throws MojoExecutionException {
        if ( sdkParam == null ) {
            sdkDir = artifactResolver.unpack( deps.sdk() );
            sdkLibDir = sdkDir.resolve( "rt/lib" );

            if ( !sdkLibDir.toFile().exists() ) {
                throw new MojoExecutionException( "It's not possible to download and install iOS SDK" );
            }
        }
        else {
            sdkDir = sdkParam.toPath();
            sdkLibDir = sdkDir.resolve( "rt/lib" );

            if ( !sdkLibDir.toFile().exists() ) {
                throw new MojoExecutionException( "Configured iOS SDK is invalid!" );
            }
        }

        log.info( "Using jfxports iOS SDK from location " + sdkDir );
    }

    private void configureRobovm( File robovmSdkParam ) throws MojoExecutionException {
        if ( robovmSdkParam == null ) {
            Dependency robovmDependency = deps.robovm();
            robovmDir = artifactResolver.unpack( robovmDependency );

            if ( !robovmDir.toFile().exists() ) {
                throw new MojoExecutionException( "RoboVM " + robovmDependency.getVersion() + " cannot be downloaded and istalled" );
            }
        }
        else {
            robovmDir = robovmSdkParam.toPath();

            if ( !robovmDir.toFile().exists() ) {
                throw new MojoExecutionException( "Configured RoboVM SDK cannot be found" );
            }
        }

        log.info( "Using robovm from location " + robovmDir );
    }

    private void configureDependencies( List<Dependency> dependencies ) throws MojoExecutionException {
        if ( dependencies != null ) {
            for ( Dependency dependency : dependencies ) {
                getUserArtifacts().add( artifactResolver.resolve( dependency ) );
            }
        }

        getBuildingArtifacts().add( artifactResolver.resolve( deps.robovmRt() ) );
        getBuildingArtifacts().add( artifactResolver.resolve( deps.cocoatouch() ) );
        getBuildingArtifacts().add( artifactResolver.resolve( deps.jfxrt() ) );
        getBuildingArtifacts().add( artifactResolver.resolve( deps.compat() ) );
        getBuildingArtifacts().add( artifactResolver.resolve( deps.objc() ) );
    }

    private void configureSimulatorArch( String archParam ) {
        if ( archParam != null ) {
            try {
                Arch archVal = Arch.valueOf( archParam );

                // thumbv7 and arm64 are not valid for simulator. Only x86 or x86_64
                if ( archVal == Arch.x86 || archVal == Arch.x86_64 ) {
                    arch = archVal;
                    return;
                }
                else {
                    log.warn( "arch=" + archParam + " is not a valid value for simulator. Use x86 or x86_64 instead" );
                }
            }
            catch ( IllegalArgumentException ex ) {
                log.warn( "arch=" + archParam + " is not valid. Valid values are x86 or x86_64" );
            }
        }
        else {
            log.info( "No arch defined" );
        }

        // try to infer architecture from operating system
        String osArch = System.getProperty( "os.arch", "arm64" );

        if ( "arm64".equals( osArch ) || "x86_64".equals( osArch ) ) {
            log.info( "arch=x86_64 is used" );
            arch = Arch.x86_64;
        }
        else {
            log.info( "arch=x86 is used" );
            arch = Arch.x86;
        }
    }

    private void configureDeviceArch( String archParam ) {
        if ( archParam != null ) {
            try {
                Arch archVal = Arch.valueOf( archParam );

                // x86 and x86_64 are not valid for devices. Only thumbv7 or arm64
                if ( archVal == Arch.thumbv7 || archVal == Arch.arm64 ) {
                    arch = archVal;
                    return;
                }
                else {
                    log.warn( "arch=" + archParam + " is not a valid value for device. Use thumbv7 or arm64 instead" );
                }
            }
            catch ( IllegalArgumentException ex ) {
                log.warn( "arch=" + archParam + " is not valid. Valid values are thumbv7 or arm64" );
            }
        }
        else {
            log.info( "No arch defined" );
        }

        log.info( "arch=thumbv7 is used" );
        arch = Arch.thumbv7;
    }

    private void configureTargetSDK( String sdkParam, boolean isSimulator ) throws MojoExecutionException {
        List<SDK> installedSDKs = isSimulator ? SDK.listSimulatorSDKs() : SDK.listDeviceSDKs();

        if ( installedSDKs.isEmpty() ) {
            throw new MojoExecutionException( "No " + ( isSimulator ? "simulator" : "device" ) + " SDKs installed" );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "List of installed iOS " + ( isSimulator ? "Simulator" : "Device" ) + " SDK:" );
            for ( SDK installedSDK : installedSDKs ) {
                log.debug( "SDK found: " + installedSDK.getDisplayName() );
            }
        }

        if ( sdkParam == null ) {
            Collections.sort( installedSDKs );
            sdk = installedSDKs.get( installedSDKs.size() - 1 );
            log.info( "No iOS SDK defined. " + sdk.getVersion() + " used" );
        }
        else {
            for ( SDK installedSDK : installedSDKs ) {
                if ( installedSDK.getVersion().equals( sdkParam ) ) {
                    sdk = installedSDK;
                }
            }

            if ( sdk == null ) {
                throw new MojoExecutionException( "No SDK found matching version string " + sdkParam );
            }
        }
    }

    private void configureSimulatorDevice( String deviceNameParam ) {
        if ( deviceNameParam != null ) {
            if ( deviceNameParam.toLowerCase().contains( "iphone" ) ) {
                deviceFamily = DeviceType.DeviceFamily.iPhone;
            }
            else if ( deviceNameParam.toLowerCase().contains( "ipad" ) ) {
                deviceFamily = DeviceType.DeviceFamily.iPad;
            }
            else {
                deviceFamily = DeviceType.DeviceFamily.iPhone;
            }
        }

        deviceName = deviceNameParam;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public DeviceType.DeviceFamily getDeviceFamily() {
        return deviceFamily;
    }

    public String getDeviceId() {
        return iosData.getDeviceId();
    }

    private void configureIpaArchs( List<String> ipaArchsParam ) throws MojoExecutionException {
        for ( String ipaArch : ipaArchsParam ) {
            try {
                getIpaArchs().add( Arch.valueOf( ipaArch ) );
            }
            catch ( IllegalArgumentException ex ) {
                throw new MojoExecutionException( "ipaArchs=" + getIpaArchs() + " contains invalid arch " + ipaArch );
            }
        }
    }

    public List<Arch> getIpaArchs() {
        return ipaArchs;
    }

    public File getInfoPList() {
        return infoPList;
    }

    public boolean isDefInfoPList() {
        return defInfoPList;
    }

    public String getApsEnvironment() {
        return iosData.getApsEnvironment();
    }

    private void configureOS( IosData iosData ) throws MojoExecutionException {
        try {
            this.os = OS.valueOf( iosData.getOs() );
        }
        catch ( IllegalArgumentException ex ) {
            throw new MojoExecutionException( "OS=" + iosData.getOs() + " is invalid. Valid values are " + Arrays.toString( OS.values() ) );
        }
    }

    public List<String> getAdditionalFrameworks() {
        return iosData.getFrameworks() != null ? iosData.getFrameworks() : new ArrayList<String>();
    }

    private void checkApsEnvironment( String apsEnvironment ) throws MojoExecutionException {
        if ( apsEnvironment != null && !"development".equals( apsEnvironment ) && !"production".equals( apsEnvironment ) ) {
            throw new MojoExecutionException( "apsEnvironment=" + apsEnvironment + " is invalid. It must be either 'development' or 'production'" );
        }
    }

    public static enum Target {
        IPA,
        SIMULATOR,
        DEVICE

    }
}
