plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    // ========== ANDROID (using new Android-KMP plugin) ==========
    androidLibrary {
        namespace = "in.sitharaj.reduxkmp.sample"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
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
    
    // JS - Browser target
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "sample.js"
            }
        }
        binaries.executable()
    }
    
    // WASM - Browser target
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "sample.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(project(":redux-kmp"))
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2") // Version matches core
            }
        }
        
        jsMain.dependencies {
            implementation(compose.html.core)
        }
    }
}

compose.desktop {
    application {
        mainClass = "in.sitharaj.reduxkmp.sample.MainKt" // Fixed package name
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "Sample App"
            packageVersion = "1.0.0"
        }
    }
}

// ========== RUN CONFIGURATIONS ==========
// Android: Run ':androidApp' configuration
// Desktop: ./gradlew :sample:run
// iOS: Open iosApp/iosApp.xcodeproj in Xcode
// JS Web: ./gradlew :sample:jsBrowserDevelopmentRun
// WASM Web: ./gradlew :sample:wasmJsBrowserDevelopmentRun
