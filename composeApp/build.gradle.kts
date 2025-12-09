import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // 添加部署目标版本设置
            linkerOpts.add("-miphoneos-version-min=15.1")
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-android:2.3.7")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
//            implementation(libs.webview.multiplatform.mobile)
            implementation("io.github.kevinnzou:compose-webview-multiplatform:2.0.3")

            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation ("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation ("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            implementation ("io.ktor:ktor-client-logging:2.3.7")
            implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        }
        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.7")
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.kmp.vayone"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.kmp.vayone"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionName = "1.0.0"
        versionCode = 100
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

