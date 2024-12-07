plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.dogmatch2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dogmatch2"
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
    buildFeatures {viewBinding = true
        mlModelBinding = true
    }
}

dependencies {
    implementation("com.lorentzos.swipecards:library:1.0.9")
    implementation(libs.firebase.core)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.android.image.cropper)
    implementation(platform(libs.firebase.bom))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.maps)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}