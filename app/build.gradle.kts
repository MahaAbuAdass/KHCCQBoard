plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("androidx.navigation.safeargs")
    id ("kotlin-parcelize")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.khccqboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.khccqboard"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.navigation:navigation-fragment:2.8.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

}