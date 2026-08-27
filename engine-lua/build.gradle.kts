plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.revolutionary.codelearn.engine.lua"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-execution"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.luaj.jse)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
}
