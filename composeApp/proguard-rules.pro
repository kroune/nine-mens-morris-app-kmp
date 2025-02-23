-optimizationpasses 5
-flattenpackagehierarchy ''
-repackageclasses ''
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keep class com.arkivanov.decompose.** { *; }

# i think proguard deletes it because of runtime engine selection
# ktor
-keep class io.ktor.** { *; }

-keep class io.github.kroune.nine_mens_morris_kmp_app.navigation.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class kotlinx.coroutines.flow.** { *; }
-keep class kotlinx.coroutines.swing.** { *; }

# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# Statically turn off all debugging facilities and assertions
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    boolean getASSERTIONS_ENABLED() return false;
    boolean getDEBUG() return false;
    boolean getRECOVER_STACK_TRACES() return false;
}

# We assume that Main Dispatcher is always present, good luck debugging this is prod
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt {
    boolean SUPPORT_MISSING return false;
}