plugins {
    alias(libs.plugins.android.application)
    id("io.freefair.lombok") version "8.13"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.luuhavyy.collabapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.luuhavyy.collabapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Firebase
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.google.firebase.storage)

    // Glide
    implementation(libs.glide)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    annotationProcessor(libs.glide.compiler)

    // Lombok dependencies
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Commons-lang3
    implementation(libs.commons.lang3)

    // Lottie - animation
    implementation(libs.android.lottie)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}