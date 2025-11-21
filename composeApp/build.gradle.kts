import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import dev.detekt.gradle.Detekt
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import kotlin.io.encoding.ExperimentalEncodingApi

object AppInfo {
    const val APP_VERSION: String = "1.0.1"
    const val APP_VERSION_INT: Int = 101
    const val LICENSE_TYPE = "GPL-3.0"
    const val DESCRIPTION = "Implementation of a table game called <Nine mens morris>"
    const val HOME_PAGE = "https://github.com/kroune/nine-mens-morris-lib-kmp"
}

tasks.withType<Detekt>().configureEach {
    exclude { element ->
        element.file.path.contains("/build/generated/")
    }
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.baseline.profile)
    alias(libs.plugins.build.konfig)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.detekt)
//    alias(libs.plugins.compose.compiler.report.generator)
//    id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha03"
//    alias(libs.plugins.storytale)
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config.setFrom("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline =
        file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.buildkonfig.gradle.plugin)
    }
}

buildkonfig {
    packageName = "io.github.kroune.nine_mens_morris_kmp_app"

    defaultConfigs {
        buildConfigField(STRING, "distribution", "")
        buildConfigField(STRING, "version", AppInfo.APP_VERSION)
        buildConfigField(INT, "versionInt", AppInfo.APP_VERSION_INT.toString())
    }
    targetConfigs {
        create("android") {
            buildConfigField(STRING, "distribution", "Android")
        }
        create("ios") {
            buildConfigField(STRING, "distribution", "Ios")
        }
        create("iosSimulatorArm64") {
            buildConfigField(STRING, "distribution", "IosSimulatorArm64")
        }
        create("iosArm64") {
            buildConfigField(STRING, "distribution", "IosArm64")
        }
        create("iosX64") {
            buildConfigField(STRING, "distribution", "IosX64")
        }
        create("desktop") {
            buildConfigField(STRING, "distribution", "Desktop")
        }
        create("wasmJs") {
            buildConfigField(STRING, "distribution", "WasmJs")
        }
    }
}

kotlin {
    cocoapods {
        version = AppInfo.APP_VERSION
        license = AppInfo.LICENSE_TYPE
        summary = AppInfo.DESCRIPTION
        homepage = AppInfo.HOME_PAGE
        framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.decompose)
            export(libs.decompose.lifecycle)

            // Optional, only if you need state preservation on Darwin (Apple) targets
            export(libs.decompose.state.keeper)
        }
        podfile = project.file("../iosApp/podfile")
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }
    compilerOptions {
        freeCompilerArgs.add("-Xnon-local-break-continue")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi")
    }

    js {
        browser()
        binaries.executable()
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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            api(libs.decompose)
            implementation(libs.decompose.animations)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.protobuf)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.serialization.kotlinx.protobuf)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.ninemensmorris)
            implementation(libs.filekit.compose)
            implementation(libs.koin.core)
//            implementation(compose.components.uiToolingPreview)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
//            implementation(compose.uiTooling)
//            implementation(compose.preview)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.multiplatform.settings.test)
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

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks.register("emptyLiner") {
    ensureSingleEmptyLineAtEndVerbose("/home/olowo/StudioProjects/nine-mens-morris-app-kmp/composeApp")
}

fun ensureSingleEmptyLineAtEnd(directoryPath: String) {
    val directory = File(directoryPath)

    if (!directory.exists() || !directory.isDirectory) {
        println("Error: The specified path is not a valid directory")
        return
    }

    val kotlinFiles = directory.walk()
        .filter { it.isFile && it.extension == "kt" }
        .toList()

    if (kotlinFiles.isEmpty()) {
        println("No Kotlin files found in the specified directory")
        return
    }

    var processedCount = 0
    var modifiedCount = 0

    kotlinFiles.forEach { file ->
        try {
            processedCount++
            val content = file.readText()

            // Remove all trailing whitespace and newlines
            val trimmedContent = content.trimEnd()

            // Determine the line separator used in the file
            val lineSeparator = detectLineSeparator(content)

            // Add exactly one empty line at the end
            val newContent = if (trimmedContent.isNotEmpty()) {
                trimmedContent + lineSeparator
            } else {
                trimmedContent // Leave empty files as-is
            }

            // Only write if content actually changed
            if (newContent != content) {
                file.writeText(newContent)
                modifiedCount++
                println("✓ Fixed trailing lines in: ${file.name}")
            }
        } catch (e: Exception) {
            println("✗ Error processing ${file.name}: ${e.message}")
        }
    }

    println("\nProcessing complete: $processedCount files processed, $modifiedCount files modified")
}

private fun detectLineSeparator(content: String): String {
    return when {
        content.contains("\r\n") -> "\r\n" // Windows
        content.contains("\r") -> "\r"     // Old Mac
        else -> "\n"                       // Unix/Linux/Mac OS X
    }
}

// Alternative version that shows what changes were made
fun ensureSingleEmptyLineAtEndVerbose(directoryPath: String) {
    val directory = File(directoryPath)

    if (!directory.exists() || !directory.isDirectory) {
        println("Error: The specified path is not a valid directory")
        return
    }

    directory.walk()
        .filter { it.isFile && it.extension == "kt" }
        .forEach { file ->
            try {
                val originalContent = file.readText()
                val originalEnding = getTrailingWhitespace(originalContent)

                // Remove all trailing whitespace and newlines
                val trimmedContent = originalContent.trimEnd()

                // Determine line separator
                val lineSeparator = detectLineSeparator(originalContent)

                // Add exactly one empty line at the end
                val newContent = if (trimmedContent.isNotEmpty()) {
                    trimmedContent + lineSeparator
                } else {
                    trimmedContent
                }

                if (newContent != originalContent) {
                    val newEnding = getTrailingWhitespace(newContent)
                    file.writeText(newContent)
                    println("✓ Fixed: ${file.name}")
                    println(
                        "  Before: ended with ${originalEnding.length} chars '${
                            escapeWhitespace(
                                originalEnding
                            )
                        }'"
                    )
                    println(
                        "  After:  ended with ${newEnding.length} chars '${
                            escapeWhitespace(
                                newEnding
                            )
                        }'"
                    )
                }
            } catch (e: Exception) {
                println("✗ Error processing ${file.name}: ${e.message}")
            }
        }
}

private fun getTrailingWhitespace(content: String): String {
    return content.takeLastWhile { it == ' ' || it == '\t' || it == '\n' || it == '\r' }
}

private fun escapeWhitespace(text: String): String {
    return text.replace("\r", "\\r")
        .replace("\n", "\\n")
        .replace("\t", "\\t")
        .replace(" ", "·")
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
        versionCode = AppInfo.APP_VERSION_INT
        versionName = AppInfo.APP_VERSION
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
                rpmLicenseType = AppInfo.LICENSE_TYPE
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
            packageVersion = AppInfo.APP_VERSION
            description = "Implementation of a table game called <Nine mens morris>"
            vendor = "kroune"
            copyright = "© 2024 Kroune. All rights reserved."
            licenseFile = project.file("LICENSE")
        }
    }
}
