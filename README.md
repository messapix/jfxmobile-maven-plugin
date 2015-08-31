# jfxmobile-maven-plugin
Maven version of jfxmobile plugin

### General

| parameter          | type   | description                                             | property                     | default |
| ------------------ | ------ | ------------------------------------------------------- | ---------------------------- | ------- |
| javafxportsVersion | String | The version of javafxPorts to use                       | jfxmobile.javafxportsVersion | 8u60    |
| mainClass          | String | The fully qualified name of the main class              | jfxmobile.mainClass          |         |
| preloaderClass     | String | The fully qualified name of the class used as preloader | jfxmobile.preloaderClass     |         |

### Android
Android parameters are wrapped in android element.

| parameter          | type   | description                                             | property                     | default |
| ------------------ | ------ | ------------------------------------------------------- | ---------------------------- | ------- |
| androidSdk         | File | The android SDK directory path                          | jfxmobile.android.androidSdk | The ANDROID_HOME system environment variable |
| dalvikSdk          | File | The directory containing the JavaFX port for dalvik     | jfxmobile.android.dalvikSdk|It will be downloaded from maven central when not specified |
| buildToolsVersion | String | The version of the Android build-tools that should be used | jfxmobile.android.buildToolsVestion | The plugin will try to look for the highest non-preview version available in the Android SDK |
| applicationPackage | String | The name of the Android package, that uniquely identifies your application | jfxmobile.android.applicationPackage | The package name of your main class name |
| manifest | File| The location of the AndroidManifest.xml file| jfxmobile.android.manifest| If no specified a default AndroidManifest file will be created |
| compileSdkVersion | String | The API version of the Android platform to compile against | jfxmobile.android.compileSdkVersion | The highest version of installed android platforms|
| minSdkVersion | String | The minimum API version where the application can run on | jfxmobile.android.minSdkVersion | 4 |
| targetSdkVersion | String | The API version that the application targets | jfxmobile.android.targetSdkVersion | 21 |
| resourceFiltering | Boolean | If maven should resolve properties in src/android/resources folder | jfxmobile.android.resourceFiltering | false |
| signingConfig | SigningConfig | A signing configuration that should be used to sign the apk. See Signing Your Applications at the Android developer guide for more information. The only difference with the android gradle plugin is, that you directly set the configuration instead of a reference to a signing configuration that was defined elsewhere. | | Default debug keystore. Note that if signingConfig is defined the final name of the APK will be *name*-release.apk otherwise it will be *name*-debug.apk |
| packagingOptions | PackagingOptions | Android APK cannot be built if there are multiple resources with the same name. This parameter allows to exclude such resources or pick the first occurrence only | | |
| dependencies | List<Dependency> | Allow user to define specific android dependencies| | |
