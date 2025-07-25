plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("io.freefair.lombok") version "8.13"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // --- AndroidX & UI ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- Navigation ---
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.ui.ktx)

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // --- Glide ---
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // --- Lombok ---
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // --- Commons Lang3 ---
    implementation(libs.commons.lang3)

    // --- Lottie Animation ---
    implementation(libs.android.lottie)

    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}
