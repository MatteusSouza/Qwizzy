import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library) // replaces android.library
    alias(libs.plugins.kotlin.compose)
}

// With the new plugin, the top-level android {} block no longer exists. The configuration now goes inside androidTarget() within the kotlin {} block."

kotlin {
    android {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }

        namespace = "com.example.askceny.shared"
        compileSdk = 36
        minSdk = 24
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {
        }
        iosMain.dependencies {
        }
    }
}