import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import kotlin.io.encoding.ExperimentalEncodingApi

val appVersion: String = "1.0.1"
val appVersionInt: Int = 101

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.baseline.profile)
//    alias(libs.plugins.compose.compiler.report.generator)
//    id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha03"
//    alias(libs.plugins.storytale)
}

composeCompiler {
    featureFlags = setOf(
        ComposeFeatureFlag.OptimizeNonSkippingGroups,
        ComposeFeatureFlag.PausableComposition
    )
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xnon-local-break-continue")
    }

    @OptIn(ExperimentalWasmDsl::class)
    listOf(wasmJs()).forEach {
        with(it) {
            compilerOptions {
                sourceMapNamesPolicy.assign(JsSourceMapNamesPolicy.SOURCE_MAP_NAMES_POLICY_SIMPLE_NAMES)
            }
            outputModuleName = "NineMensMorrisApp"
            browser {
                testTask {
                    useKarma {
                        useDebuggableChrome()
                    }
                }
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.decompose)
            implementation(libs.decompose.jetbrains)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.protobuf)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.ninemensmorris)
            implementation(libs.filekit.compose)
            implementation(libs.koin.core)
//            implementation(compose.components.uiToolingPreview)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
//            implementation(compose.uiTooling)
//            implementation(compose.preview)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
//            implementation(compose.uiTooling)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

tasks.register("wasmJsProcessBrowserDistribution") {
    dependsOn("wasmJsBrowserDistribution")
    val dir = "build/dist/wasmJs/productionExecutable"
    val absolutePath = projectDir.absolutePath
    inputs.dir(dir)
    outputs.dir(dir)
    description = "Rename wasm files"
    doLast {
        val file = File(absolutePath, dir)
        if (file.exists()) {
            val fileToParse = File(file, "composeApp.js")
            val fileText = fileToParse.readText()
            val regex = Regex("e\\.exports=r\\.p\\+\"[a-zA-Z0-9]*\\.wasm\"")
            val prefix = "e.exports=r.p+\""
            val suffix = "\""
            val matches = regex.findAll(fileText).toList()
                .map { it.value.removePrefix(prefix).removeSuffix(suffix) }
            require(matches.size == 2)
            val app = matches[0]
            val newAppName = "app.wasm"
            val skiko = matches[1]
            val newSkikoName = "skiko.wasm"
            println("app - $app, skiko - $skiko")
            assert(File(file, app).renameTo(File(file, newAppName)))
            assert(File(file, skiko).renameTo(File(file, newSkikoName)))
            val transformedText = fileText.replace(app, newAppName).replace(skiko, newSkikoName)
            fileToParse.writeText(transformedText)
        } else {
            logger.error("empty")
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
android {
    namespace = "io.github.kroune.nine_mens_morris_kmp_app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.github.kroune.nine_mens_morris"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionInt
        versionName = appVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "DebugProbesKt.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = "release"
            if (System.getenv("KEYSTORE") != null && System.getenv("KEYSTORE_PASSWORD") != null) {
                storeFile = File(project.projectDir.absolutePath, "keyStore.jks")
                storePassword = System.getenv("KEYSTORE_PASSWORD")!!
                keyPassword = System.getenv("KEYSTORE_PASSWORD")!!
            } else {
                storeFile = file("/home/olowo/secureKeystore.jks")
                storePassword = file("/home/olowo/secureSignPass").readText().trim()
                keyPassword = file("/home/olowo/secureSignPass").readText().trim()
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }
    buildFeatures {
        compose = true
    }
    //https://developer.android.com/develop/ui/compose/testing#setup
    dependencies {
        debugImplementation(libs.androidx.ui.test.manifest)
        androidTestImplementation(libs.androidx.ui.test)
        "baselineProfile"(project(":baselineprofile"))
    }
    baselineProfile {
        baselineProfileOutputDir = "../androidMain/generated/baselineProfiles"
        automaticGenerationDuringBuild = true
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel2api30") {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 2"
                    // Use only API levels 27 and higher.
                    apiLevel = 30
                    // To include Google services, use "google".
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

compose.desktop {
    application {
        buildTypes.release.proguard {
            configurationFiles.setFrom("src/desktopMain/proguard-jvm-rules.pro")
            version.set("7.6.1")
            isEnabled = true
            obfuscate.set(true)
            optimize.set(true)
        }
        mainClass = "io.github.kroune.nine_mens_morris_kmp_app.MainKt"

        nativeDistributions {
            macOS {
                iconFile = project.file("icons/icon.icns")
            }
            linux {
                debMaintainer = "kr0ne@tuta.io"
                appCategory = "Amusements/Games"
                rpmLicenseType = "GPL-3.0"
                modules("jdk.security.auth")
                iconFile = project.file("icons/icon.png")
            }
            windows {
                menuGroup = "start-menu-group"
                iconFile = project.file("icons/icon.ico")
            }
            targetFormats(
                // macos
                TargetFormat.Dmg,
                // linux
                TargetFormat.Rpm, TargetFormat.Deb,
                // windows
                TargetFormat.Msi, TargetFormat.Exe
            )
            packageName = "NineMensMorris"
            packageVersion = appVersion
            description = "Implementation of a table game called <Nine mens morris>"
            vendor = "kroune"
            copyright = "© 2024 Kroune. All rights reserved."
            licenseFile = project.file("LICENSE")
        }
    }
}
