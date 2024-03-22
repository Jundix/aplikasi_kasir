plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.aplikasi_kasir"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aplikasi_kasir"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary=true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.activity:activity-ktx:1.7.0")
    implementation ("id.zelory:compressor:3.0.1")
//    implementation ("com.github.bumptech.glide:glide:4.16.0")
//    implementation ("com.squareup.picasso:picasso:2.8")
//    implementation ("com.nostra13.universalimageloader:universal-image-loader:1.9.5")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("com.github.DantSu:ESCPOS-ThermalPrinter-Android:3.3.0")
    implementation ("com.github.anggastudio:Printama:0.9.7")
    implementation ("com.pranavpandey.android:dynamic-utils:4.5.1")





}