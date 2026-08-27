plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.revolutionary.codelearn.core.model"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
