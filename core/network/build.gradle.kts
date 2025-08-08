plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)

    id("kotlin-parcelize")
}

android {
    namespace = "com.work.network"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
//    implementation(project(":core:base"))

    //retrofit
    api(libs.retrofit2)
    api(libs.retrofit2.gsonConverter)
    api(libs.retrofit2.okhttp)
    api(libs.retrofit2.okhttpLoggingInterceptor)

    api(libs.google.code.gson)

    //coroutines
    implementation(libs.kotlin.coroutines)

    //koin
    api(project.dependencies.platform(libs.koin.bom))
    api(libs.koin.core)
    api(libs.koin.android)

    //room
    api(libs.room)
    ksp(libs.room.ksp)
    api(libs.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}