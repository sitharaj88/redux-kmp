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

include(":redux-kmp")
include(":sample")
include(":androidApp")
