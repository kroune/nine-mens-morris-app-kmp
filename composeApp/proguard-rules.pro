-keep class com.arkivanov.decompose.** { *; }
# i think proguard deletes it because of runtime engine selection
-keep class io.ktor.client.engine.cio.** { *; }
-keep class com.kroune.nine_mens_morris_kmp_app.navigation.** { *; }
-keep class com.kroune.nineMensMorrisLib.Position { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class kotlinx.coroutines.flow.** { *; }
-keep class kotlinx.coroutines.swing.** { *; }
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }