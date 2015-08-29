# jfxmobile-maven-plugin
Maven version of jfxmobile plugin

### General

| parameter          | type   | description                                             | property                     | default |
| ------------------ | ------ | ------------------------------------------------------- | ---------------------------- | ------- |
| javafxportsVersion | String | The version of javafxPorts to use                       | jfxmobile.javafxportsVersion | 8u60    |
| mainClass          | String | The fully qualified name of the main class              | jfxmobile.mainClass          |         |
| preloaderClass     | String | The fully qualified name of the class used as preloader | jfxmobile.preloaderClass     |         |

### Android
Android parameters are wrapped in <android> element.
| parameter          | type   | description                                             | property                     | default |
| ------------------ | ------ | ------------------------------------------------------- | ---------------------------- | ------- |
| androidSdk         | File | The android SDK directory path                          | jfxmobile.android.androidSdk | The ANDROID_HOME system environment variable |
| dalvikSdk          | File | The directory containing the JavaFX port for dalvik     | It will be downloaded from maven central when not specified |
| buildToolsVersion | String | The version of the Android build-tools that should be used | The plugin will try to look for the highest non-preview version available in the Android SDK |
| applicationPackage | String | The name of the Android package, that uniquely identifies your application | The package name of your main class name
