import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baseline.profile)
}

baselineProfile {
    managedDevices += "pixel2Api34"
    useConnectedDevices = false
}

android {
    namespace = "io.github.kroune.baselineprofile"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    defaultConfig {
        minSdk = 28
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":composeApp"

    // This code creates the gradle managed device used to generate baseline profiles.
    // To use GMD please invoke generation through the command line:
    // ./gradlew :composeApp:generateBaselineProfile
    testOptions.managedDevices.devices {
        @Suppress("UnstableApiUsage")
        create<ManagedVirtualDevice>("pixel2Api34") {
            device = "Pixel 2"
            apiLevel = 34
            systemImageSource = "aosp-atd"
        }
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.macro.benchmark)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            @Suppress("UnstableApiUsage")
            v.testedApks.map { artifactsLoader.load(it)?.applicationId!! }
        )
    }
}
