plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("io.gitlab.arturbosch.detekt")
    id("com.google.devtools.ksp")
}
val TWITCH_CLIENT_ID by extra("18co1vlea403qe8lltwkesrmlw7hs0")
val TWITCH_AUTHORIZATION by extra("bx8cpl4zq1p0lp9dmbxwdqp3y2c5pa")

android {
    namespace = "com.cuervolu.witcherscodex"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cuervolu.witcherscodex"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("TWITCH_CLIENT_ID")}\"")
            buildConfigField("String", "Authorization", "\"${System.getenv("TWITCH_AUTHORIZATION")}\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("TWITCH_CLIENT_ID")}\"")
            buildConfigField("String", "Authorization", "\"${System.getenv("TWITCH_AUTHORIZATION")}\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")
    //Chip Navigation
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    //Spinkit
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")
    //Auth
    implementation("com.google.firebase:firebase-auth-ktx")
    //Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")
    //Storage
    implementation("com.google.firebase:firebase-storage-ktx")
    //Facebook SDK
//    implementation("com.facebook.android:facebook-android-sdk:16.2.0")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
    //Lottie
    implementation("com.airbnb.android:lottie:6.1.0")
    //Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("com.google.firebase:firebase-appcheck:17.1.1")
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.2")
    //ksp
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.20-1.0.14")
    //UCrop
    implementation("com.github.yalantis:ucrop:2.2.6")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    //Compressor
    implementation("id.zelory:compressor:3.0.1")

    //SmoothBottomBar
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    // ScrollView
    implementation("com.github.woxingxiao:BounceScrollView:1.5-androidx")
}