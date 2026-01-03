import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

val libraryVersion: String = project.findProperty("VERSION_NAME") as? String ?: "1.0.0"
val libraryArtifactId: String = project.findProperty("POM_ARTIFACT_ID") as? String ?: "library"

group = project.findProperty("GROUP") as? String ?: "com.example"
version = libraryVersion

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()
    
    // Suppress expect/actual classes beta warning
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    
    // ========== ANDROID (using new Android-KMP plugin) ==========
    androidLibrary {
        namespace = project.findProperty("LIBRARY_PACKAGE") as? String ?: "com.sitharaj.reduxkmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========== DESKTOP (JVM) ==========
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========== iOS ==========
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = libraryArtifactId.replaceFirstChar { it.uppercase() }
            isStatic = true
        }
    }
    
    // ========== WEB (JS) ==========
    js(IR) {
        browser()
        nodejs()
    }
    
    // ========== WASM ==========
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    // ========== DEPENDENCIES ==========
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.kotlinx.coroutines.core)
        }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.runtime)
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

// ========== MAVEN CENTRAL PUBLISHING ==========

val dokkaHtmlTask = tasks.named("dokkaHtml")

publishing {
    publications.withType<MavenPublication> {
        // Create a unique javadoc jar for each publication to avoid task ordering issues
        val publicationName = name
        val javadocJarTask = tasks.register<Jar>("${publicationName}JavadocJar") {
            archiveClassifier.set("javadoc")
            archiveBaseName.set("${libraryArtifactId}-${publicationName}")
            from(dokkaHtmlTask)
        }
        artifact(javadocJarTask)
        
        pom {
            // CUSTOMIZE: Update these for your library
            name.set(project.findProperty("LIBRARY_NAME") as? String ?: "MyLibrary")
            description.set("A Kotlin Multiplatform library")
            url.set("https://github.com/sitharaj88/${libraryArtifactId}-kmp")
            
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            
            developers {
                developer {
                    id.set("sitharaj88")
                    name.set("Sitharaj Seenivasan")
                    email.set("sitharaj88@gmail.com")
                }
            }
            
            scm {
                url.set("https://github.com/sitharaj88/${libraryArtifactId}-kmp")
                connection.set("scm:git:git://github.com/sitharaj88/${libraryArtifactId}-kmp.git")
                developerConnection.set("scm:git:ssh://git@github.com/sitharaj88/${libraryArtifactId}-kmp.git")
            }
        }
    }
    
    repositories {
        maven {
            name = "LocalStaging"
            url = uri(layout.buildDirectory.dir("staging"))
        }
    }
}

signing {
    // Signing configured via ~/.gradle/gradle.properties:
    // signing.keyId=
    // signing.password=
    // signing.secretKeyRingFile=
    sign(publishing.publications)
}

// ========== BUNDLE TASK FOR MAVEN CENTRAL ==========

tasks.register<Zip>("zipBundle") {
    dependsOn("publishAllPublicationsToLocalStagingRepository")
    
    from(layout.buildDirectory.dir("staging"))
    archiveFileName.set("${libraryArtifactId}-bundle.zip")
    destinationDirectory.set(layout.buildDirectory.dir("bundle"))
}

tasks.register<Zip>("zipBundleUnsigned") {
    dependsOn(tasks.matching { it.name.startsWith("publish") && it.name.endsWith("ToLocalStagingRepository") && !it.name.contains("sign", ignoreCase = true) })
    
    from(layout.buildDirectory.dir("staging"))
    archiveFileName.set("${libraryArtifactId}-bundle-unsigned.zip")
    destinationDirectory.set(layout.buildDirectory.dir("bundle"))
}
