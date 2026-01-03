pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// ============================================
// CUSTOMIZE: Change project name
// ============================================
rootProject.name = "reduxkmp-kmp"

include(":library")
include(":sample")
include(":androidApp")
