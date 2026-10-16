plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "ci.scrabble2g"
    compileSdk = 35
    defaultConfig {
        applicationId = "ci.scrabble2g"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
