import org.gradle.kotlin.dsl.implementation
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
            implementation(libs.ktor.ktor.client.android)
            implementation(libs.appsflyer.android)
            implementation(libs.installreferrer)
//            implementation(libs.github.xxpermissions)
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
            implementation(libs.compose.webview.multiplatform)

            implementation(libs.ktor.client.core)
            implementation (libs.ktor.ktor.client.content.negotiation)
            implementation (libs.ktor.ktor.serialization.kotlinx.json)
            implementation (libs.ktor.ktor.client.logging)
            implementation (libs.kotlinx.coroutines.core)
            implementation (libs.kotlinx.serialization.json)

            implementation(libs.permissions)
            implementation(libs.moko.permissions.compose)
            implementation("dev.icerock.moko:permissions-camera:0.20.1")
            implementation("dev.icerock.moko:permissions-location:0.20.1")
            implementation("dev.icerock.moko:permissions-notifications:0.20.1")

        }
        iosMain {
            dependencies {
                implementation(libs.ktor.ktor.client.darwin)
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

