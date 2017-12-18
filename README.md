
# jfxmobile-maven-plugin
Maven version of jfxmobile plugin

Waiting for a better documentation take a look at the [JavafxPorts](http://docs.gluonhq.com/javafxports) project documentation.

### Gettin started
The following is an example of configuration:

```
...
<plugin>
  <groupId>com.messapix.ftatr.jfxmobile</groupId>
  <artifactId>jfxmobile-maven-plugin</artifactId>
  <version>1.0.0-b1</version>
  <extensions>true</extensions>
  <configuration>
      <mainClass>test.something.App</mainClass>
      <android>
          <manifest>lib/android/AndroidManifest.xml</manifest>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-android</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </android>
      
      <desktop>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-desktop</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </desktop>
      
      <ios>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-ios</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </ios>
  </configuration>
</plugin>
...
```

Note that you must set ```<extensions>true</extensions>``` in order to work with the plugin.

To compile android apk run:
```mvn clean package android```

### General

| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
| javafxportsVersion | (String) The version of javafxPorts to use | jfxmobile.javafxportsVersion | 8.60.9 |
| mainClass | (String) The fully qualified name of the main class | jfxmobile.mainClass |  |
| preloaderClass | (String) The fully qualified name of the class used as preloader | jfxmobile.preloaderClass |  |

### Android
Android parameters are wrapped in ```<android>``` element.

| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
| androidSdk | (File) The android SDK directory path | jfxmobile.android.androidSdk | The ANDROID_HOME system environment variable |
| dalvikSdk | (File) The directory containing the JavaFX port for dalvik | jfxmobile.android.dalvikSdk | It will be downloaded from maven central when not specified |
| buildToolsVersion | (String) The version of the Android build-tools that should be used | jfxmobile.android.buildToolsVersion | The plugin will try to look for the highest non-preview version available in the Android SDK |
| applicationPackage | (String) The name of the Android package, that uniquely identifies your application | jfxmobile.android.applicationPackage | The package name of your main class name |
| manifest | (File) The location of the AndroidManifest.xml file | jfxmobile.android.manifest | If no specified a default AndroidManifest file will be created |
| compileSdkVersion | (String) The API version of the Android platform to compile against | jfxmobile.android.compileSdkVersion | The highest version of installed android platforms |
| minSdkVersion | (String) The minimum API version where the application can run on | jfxmobile.android.minSdkVersion |  |
| targetSdkVersion | (String) The API version that the application targets | jfxmobile.android.targetSdkVersion |  |
| resourceFiltering | (boolean) If true maven will resolve properties in every file from src/android/resources folder | jfxmobile.android.resourceFiltering |  |
| processJava8Dependencies | (boolean)  If true the plugin try to apply retrolambda to every Java 8 dependencies. Other than retrolambda it try to backport defender methods as well, but with some [limitation](https://github.com/orfjackal/retrolambda#known-limitations). | jfxmobile.android.processJava8Dependencies |  |
| signingConfig | (SigningConfig)  A signing configuration that should be used to sign the apk. See Signing Your Applications at the Android developer guide for more information. The only difference with the android gradle plugin is, that you directly set the configuration instead of a reference to a signing configuration that was defined elsewhere |  | Default debug keystore. Note that if signingConfig is defined the final name of the APK will be *name*-release.apk otherwise it will be *name*-debug.apk |
| packagingOptions | (PackagingOptions) Android APK cannot be built if there are multiple resources with the same name. This parameter allows to exclude such resources or pick the first occurrence only |  |  |
| dependencies | (List) Allow user to define android specific dependencies |  |  |

### Desktop
Desktop parameters are wrapped in ```<desktop>``` element.

| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
| resourceFiltering | (Boolean) If true maven will resolve properties in every file from src/desktop/resources folder | jfxmobile.desktop.resourceFiltering |  |
| dependencies | (List) Allow user to define desktop specific dependencies |  |  |

### iOS
iOS parameters are wrapped in ```<ios>``` element.

| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
| resourceFiltering | (Boolean) If true maven will resolve properties in every file from src/ios/resources folder | jfxmobile.ios.resourceFiltering |  |
| dependencies | (List) Allow user to define iOS specific dependencies |  |  |
| iosSdk | (File) The directory containing the JavaFX port for ios | jfxmobile.ios.iosSdk | It will be downloaded from maven central when not specified |
| launcherClassName | (String) Used to define a custom laucher class for robovm | jfxmobile.ios.launcherClassName | The plugin generates a default launcher |
| forceLinkClasses | (List) A list of string patterns for classes and/or complete packages that should be linked when starting the RoboVM compiler. See http://docs.robovm.com/configuration.html#-lt-forcelinkclasses-gt for more information about which patterns can be used. |  |  |
| infoPList | (File) Custom Info.plist file to use | jfxmobile.ios.infoPList | The plugin will generate a default Default-Info.plist file. A copy can be found in build/javafxports/ios/tmp |
| robovmPropertiesFile | (File) The path to a RoboVM properties file which contains info for the application | jfxmobile.ios.robovmPropertiesFile | The plugin will try to search for robovm.properties and robovm.local.properties in src/ios folder |
| robovmConfigFile | (File) The path to a RoboVM configuration file which configures the RoboVM compiler | jfxmobile.ios.robovmConfigFile | The plugin will try to search for robovm.xml in src/ios folder |
| os | (String) The operating system to use when running the application. Can be any of the following values: ios, macosx, linux | jfxmobile.ios.os |  |
| skipSigning | (Boolean) A boolean specifying whether signing of the application should be skipped or not | jfxmobile.ios.skipSigning |  |
| signIdentity | (String) The name of the identity to sign with when building an iOS bundle for the application | jfxmobile.ios.signIdentity | Default is to look for an identity starting with ‘iPhone Developer’ or ‘iOS Development’ |
| provisioningProfile | (String) The name of the provisioning profile to use when building for device | jfxmobile.ios.provisioningProfile |  |
| debug | (Boolean) A boolean specifying whether the application should be launched in debug mode or not. The application will suspend before the main method is called and will wait for a debugger to connect | jfxmobile.ios.debug |  |
| debugPort | (Integer) An integer specifying the port to listen for debugger connections on when launching in debug mode | jfxmobile.ios.debugPort | If not set a default port will be used. The port actually used will be written to the console before the app is launched |
| ipaArchs | (List) A list of architectures to include in the IPA. Either thumbv7 or arm64 or both.If used in property archs must be separated by a colon. For example jfxmobile.ios.ipaArchs=thumbv7:arm64 | jfxmobile.ios.ipaArchs |  |
| simulator | (Simulator) This element contains simulator settings. See table below |  |  |

### iOS Simulator
The following parameters are used mostly as properties passed to maven at runtime. Anyway they can be set in pom.xml as well.

| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
| deviceName | (String) The name of the device to use in the iOS simulator. For instance iPhone4 or iPhone-5s | jfxmobile.ios.simulator.deviceName |  |
| arch | (String) The architecture of the target simulator. Allowed values ar x86 or x86_64 | jfxmobile.ios.simulator.arch | The architecture of the local machine |
| sdk | (String) The iOS sdk version to use in the iOS simulator | jfxmobile.ios.simulator.sdk | The highest version of installed sdks |
