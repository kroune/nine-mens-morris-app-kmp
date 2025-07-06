import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.storytale)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xnon-local-break-continue")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi")
    }

    @OptIn(ExperimentalWasmDsl::class)
    listOf(wasmJs()).forEach {
        with(it) {
            outputModuleName = "NineMensMorrisApp2"
            browser {
                val rootDirPath = project.rootDir.path
                val projectDirPath = project.projectDir.path
                commonWebpackConfig {
                    outputFileName = "composeApp.js"
                    devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static = (static ?: mutableListOf()).apply {
                            // Serve sources to debug inside browser
                            add(rootDirPath)
                            add(projectDirPath)
                        }
                    }
                }
            }
            binaries.executable()
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_23)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(project(":composeApp"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
android {
    namespace = "io.github.kroune.nine_mens_morris_kmp_app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testOptions.targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "DebugProbesKt.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }
    buildFeatures {
        compose = true
    }
}
