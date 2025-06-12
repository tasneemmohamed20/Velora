plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo") version "4.2.0"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")

}

apollo {
    service("service1") {
        packageName.set("com.example.m_commerce.service1")
        schemaFile.set(file("src/main/graphql/Admin/AdminSchema.graphqls"))
        sourceFolder.set("Admin")
        generateKotlinModels.set(true)
    }
    service("service2") {
        packageName.set("com.example.m_commerce.service2")
        schemaFile.set(file("src/main/graphql/Store/schema.graphqls"))
        sourceFolder.set("Store")
        generateKotlinModels.set(true)
    }
}

android {
    namespace = "com.example.m_commerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.m_commerce"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.maps)
    implementation (libs.maps.compose)
    implementation(libs.places)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.okhttp)
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)
    implementation(libs.apollo.runtime)
    implementation (libs.material)

    //nav
    implementation (libs.androidx.navigation.compose.v290)

    //icons
    implementation (libs.androidx.material.icons.extended)




    // Navigation compose
    implementation (libs.androidx.navigation.compose)
    //Serialization for NavArgs
    implementation (libs.kotlinx.serialization.json)

    // Glide
    implementation(libs.compose)

    implementation(libs.coil.compose)

    // work manager
    implementation(libs.androidx.work.runtime.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Hilt Navigation Compose
    implementation(libs.androidx.hilt.navigation.compose)

    // country code picker
    implementation(libs.compose.country.code.picker)


    // Firebase
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)


    implementation ("com.google.code.gson:gson:2.10.1")

    //slider
    implementation("androidx.compose.material:material:1.9.0-alpha04")

}