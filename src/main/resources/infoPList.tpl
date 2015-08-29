<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
        <key>CFBundleIdentifier</key>
        <string>${mainClass}</string>
        <key>CFBundleVersion</key>
        <string>1.0</string>
        <key>CFBundleExecutable</key>
        <string>${executableName}</string>
        <key>CFBundleName</key>
        <string>${executableName}</string>
        <key>CFBundlePackageType</key>
        <string>APPL</string>
        <key>LSRequiresIPhoneOS</key>
        <true/>
        <key>UISupportedInterfaceOrientations</key>
        <array>
            <string>UIInterfaceOrientationPortrait</string>
            <string>UIInterfaceOrientationLandscapeLeft</string>
            <string>UIInterfaceOrientationLandscapeRight</string>
            <string>UIInterfaceOrientationPortraitUpsideDown</string>
        </array>
        <key>UISupportedInterfaceOrientations~ipad</key>
        <array>
            <string>UIInterfaceOrientationPortrait</string>
            <string>UIInterfaceOrientationLandscapeLeft</string>
            <string>UIInterfaceOrientationLandscapeRight</string>
            <string>UIInterfaceOrientationPortraitUpsideDown</string>
        </array>
        <key>UIRequiredDeviceCapabilities</key>
        <array>
            <string>armv7</string>
        </array>
        <key>UIDeviceFamily</key>
        <array>
        <#if families??>
        <#list families as family>
            <integer>${family}</integer>
        </#list>
        </#if>
        </array>
    </dict>
</plist>