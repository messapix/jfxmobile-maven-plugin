package ${package};

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.uikit.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Default RoboVM Launcher class to launch JavaFX applications on iOS.
 *
 * NOTE: This file should not be edited as it will be overwritten each time you
 * run an iOS specific gradle task. If you want to create a custom Launcher, you
 * should instead create the class yourself and set the property
 * <code>jfxmobile.ios.launcherClassName</code> in your build.gradle file to the
 * fully qualified class name of your custom Launcher, e.g.:
 *
 * <pre>jfxmobile {
 *     ios {
 *         launcherClassName = 'org.sample.ios.MyCustomLauncher'
 *     }
 * }</pre>
 */
public class ${launcherName} extends UIApplicationDelegateAdapter {

    private static final String IOS_PROPERTY_PREFIX = "ios.";
    private static final String JAVAFX_PLATFORM_PROPERTIES = "javafx.platform.properties";
    private static final String JAVA_CUSTOM_PROPERTIES = "java.custom.properties";

    private static final Class<? extends Application> mainClass = ${mainClass}.class;
    private static final Class<? extends Preloader> preloaderClass = <#if preloaderClass?has_content>${preloaderClass}.class<#else>null</#if>;

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {

        Thread launchThread = new Thread() {
            @Override
            public void run() {
                if (launchOptions != null && launchOptions.getDictionary() != null) {
                    try {
                        Map<String, Object> map = launchOptions.getDictionary().asStringMap();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            
                            // TODO: Define all the (12+) different possible keys
                            switch (entry.getKey()) {
                                case "UIApplicationLaunchOptionsURLKey":
                                    System.setProperty("Launch.URL", entry.getValue().toString());
                                    break;
                                case "UIApplicationLaunchOptionsLocalNotificationKey": 
                                    if (launchOptions.getLocalNotification() != null) {
                                        final NSDictionary userInfo = launchOptions.getLocalNotification().getUserInfo();
                                        if (userInfo.containsKey("userId")) {
                                            System.setProperty("Launch.LocalNotification", userInfo.getString("userId"));
                                        }
                                    }
                                    break;
                                case "UIApplicationLaunchOptionsRemoteNotificationKey":
                                    if (launchOptions.getRemoteNotification() != null) {
                                        final NSDictionary dictionary = launchOptions.getRemoteNotification().getDictionary();
                                        // TODO: Define expected json key-values
                                        System.setProperty("Launch.PushNotification", dictionary.toString());
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Exception in didFinishLaunching: " + e);
                    }
                }

                if (Application.class.isAssignableFrom(mainClass)) {
                    LauncherImpl.launchApplication(mainClass, preloaderClass, new String[]{});
                } else {
                    try {
                        Method mainMethod = mainClass.getMethod("main", new Class<?>[]{(new String[0]).getClass()});
                        mainMethod.invoke(null, new Object[]{new String[]{}});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        launchThread.setDaemon(true);
        launchThread.start();

        return true;
    }

    public static void main(String[] args) throws Exception {
        InputStream isJavafxPlatformProperties = null;
        try {
            isJavafxPlatformProperties = ${launcherName}.class.getResourceAsStream("/" + JAVAFX_PLATFORM_PROPERTIES);
            if (isJavafxPlatformProperties == null) {
                throw new RuntimeException("Could not find /" + JAVAFX_PLATFORM_PROPERTIES + " on classpath.");
            }

            Properties platformProperties = new Properties();
            platformProperties.load(isJavafxPlatformProperties);
            for (Map.Entry<Object, Object> e : platformProperties.entrySet()) {
                String key = (String) e.getKey();
                System.setProperty(key.startsWith(IOS_PROPERTY_PREFIX)
                        ? key.substring(IOS_PROPERTY_PREFIX.length()) : key,
                        (String) e.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load " + JAVAFX_PLATFORM_PROPERTIES, e);
        } finally {
            try {
                if (isJavafxPlatformProperties != null) {
                    isJavafxPlatformProperties.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // try loading java custom properties
        InputStream isJavaCustomProperties = null;
        try {
            isJavaCustomProperties = ${launcherName}.class.getResourceAsStream("/" + JAVA_CUSTOM_PROPERTIES);
            if (isJavaCustomProperties != null) {
                Properties javaCustomProperties = new Properties();
                javaCustomProperties.load(isJavaCustomProperties);
                for (Map.Entry<Object, Object> entry : javaCustomProperties.entrySet()) {
                    System.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isJavaCustomProperties != null) {
                try {
                    isJavaCustomProperties.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.getProperties().list(System.out);

        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, ${launcherName}.class);
        }
    }
}
