# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Proguard pluginx for release
-keep class androidx.core.app.CoreComponentFactory { *; }

-dontwarn com.pluginx.**

# natvie 方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep interface com.pluginx.core.base.SDKComponent$IGameThreadCallBack{}

-keep public class com.pluginx.core.base.SDKComponent {
    public<methods>;
}

-keep class com.pluginx.core.base.FunctionHelper {
    public <methods>;
}

-keep public class com.pluginx.core.ScriptCallJavaBridge {
    public <methods>;
}

-keepnames public class com.pluginx.core.component.Component

-keep public class com.pluginx.core.component.PluginError {
    <fields>;
    public <methods>;
}

-keep public class com.pluginx.core.component.PluginResult {
    <fields>;
    public <methods>;
}

-keep public class * extends com.pluginx.core.component.Component {
    public<methods>;
}

-keepclassmembernames class * extends com.pluginx.core.component.PluginResult {
    <fields>;
    public <methods>;
}

-keep public class * extends com.pluginx.core.component.PluginWrapper {
    protected <methods>;
    public <methods>;
    public <fields>;
    protected <fields>;
}

# share
-keep class com.pluginx.core.component.ShareWrapper$ShareInfo {
    <fields>;
    public <methods>;
}
-keep class com.pluginx.core.component.ShareWrapper$ShareContentType {
    <fields>;
}

# ad
-keep class com.pluginx.core.component.AdsWrapper$AdState {*;}
-keep class com.pluginx.core.component.AdsWrapper$AdType {*;}
