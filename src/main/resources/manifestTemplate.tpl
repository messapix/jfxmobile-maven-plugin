<?xml version="1.0" encoding="UTF-8"?>

<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="${packageName}" android:versionCode="1" android:versionName="${version}">
    <supports-screens android:xlargeScreens="true"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-sdk android:minSdkVersion="${minSdkVersion}" android:targetSdkVersion="${targetSdkVersion}"/>
    <application android:label="${projectName}" android:name="android.support.multidex.MultiDexApplication">
        <activity android:name="javafxports.android.FXActivity" android:label="${projectName}" android:configChanges="orientation|screenSize">
            <meta-data android:name="main.class" android:value="${mainClass}"/>
            <#if preloaderClass?has_content>
            <meta-data android:name="preloader.class" android:value="${preloaderClass}"/>
            </#if>
            <meta-data android:name="debug.port" android:value="0"/>
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>