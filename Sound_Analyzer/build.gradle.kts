buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://chaquo.com/maven")  // Chaquopy repository
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2")  // Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")  // Kotlin Gradle Plugin, include only if Kotlin is used
        classpath("com.chaquo.python:gradle:15.0.1")  // Chaquopy Plugin for Python integration
    }
}

subprojects {
    plugins.withId("com.android.application") {
        // Configuration specific to the Android application plugin can be placed here if needed
    }
}
