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
            implementation(libs.github.xxpermissions)
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

            implementation(libs.coil.compose)
            implementation(libs.coil3.coil.network.ktor)
            implementation(libs.accompanist.swiperefresh)

            implementation(libs.kotlinx.datetime)

            implementation(libs.peekaboo.ui)
//            implementation(libs.peekaboo.image.picker)
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.ktor.client.darwin)
//                implementation("io.coil-kt.coil3:coil-network-url:3.0.0")
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
        applicationId = "com.vay.bay.inb.improvement"
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
