plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mavalore.tricenari"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mavalore.tricenari"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("androidx.media3:media3-common:1.3.1")
    implementation("androidx.activity:activity:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //circular image dependency
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //lottie animation dependency
    implementation ("com.airbnb.android:lottie:6.4.0")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //SafeArgs
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.51")
    kapt ("com.google.dagger:hilt-android-compiler:2.51")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    //shimmer effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    //Image loading library
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Facebook SDK
    implementation("com.facebook.android:facebook-login:15.2.0")
}
