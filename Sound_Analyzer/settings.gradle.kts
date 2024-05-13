pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://chaquo.com/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://mvn.0110.be/releases") }
        maven { url = uri("https://chaquo.com/maven") }
    }
    versionCatalogs {
        create("libs") {
            plugin("androidApplication", "com.android.application")
            library("appcompat", "androidx.appcompat:appcompat:1.4.1")
            library("material", "com.google.android.material:material:1.6.0")
            library("constraintLayout", "androidx.constraintlayout:constraintlayout:2.1.3")
            library("fragment", "androidx.navigation:navigation-fragment-ktx:2.4.2")
            library("ui", "androidx.navigation:navigation-ui-ktx:2.4.2")
            library("junit", "junit:junit:4.13.2")
            library("extJunit3", "androidx.test.ext:junit:1.1.3")
            library("roboElectric", "org.robolectric:robolectric:4.5")
            library("espresso", "androidx.test.espresso:espresso-core:3.4.0")
            library("tensorflowLite", "org.tensorflow:tensorflow-lite:2.9.0")
            library("tensorflowLiteGpu", "org.tensorflow:tensorflow-lite-gpu:2.9.0")
            library("tarsosDSP", "be.tarsos.dsp:core:2.5")
            library("mockito","org.mockito:mockito-core:3.11.2")
        }
    }
}
rootProject.name = "Sound_Analyzer"
include(":app")
