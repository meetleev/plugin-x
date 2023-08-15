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

# Proguard GameBase for release
-keep public class com.pluginx.core.ScriptCallJavaBridge { *; }
-dontwarn com.pluginx.**

# natvie 方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class com.pluginx.core.base.SDKComponent {
    public<methods>;
}

-keep public class com.pluginx.core.component.UserWrapper {
    public<methods>;
}

-keep class com.pluginx.core.component.UserWrapper$* {
    <fields>;
}

-keepnames public class com.pluginx.core.component.Component
-keep public class com.pluginx.core.component.PluginError {
    <fields>;
}
-keepnames public class com.pluginx.core.component.PluginWrapper