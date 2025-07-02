import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo") version "4.2.0"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
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
        all{
            val keystoreFile = project.rootProject.file("api_key.properties")
            val properties = Properties()
            properties.load(keystoreFile.inputStream())

            val adminUrl = properties.getProperty("ADMIN_URL") ?: ""
            val storeUrl = properties.getProperty("STOREFRONT_URL") ?: ""
            val adminToken = properties.getProperty("ADMIN_ACCESS_TOKEN") ?: ""
            val storeToken = properties.getProperty("STOREFRONT_ACCESS_TOKEN") ?: ""

            val publicKey = properties.getProperty("PUBLIC_KEY") ?: ""
            val secretKey = properties.getProperty("SECRET_KEY") ?: ""
            val apiKey = properties.getProperty("API_KEY") ?: ""
            val baseUrl = properties.getProperty("BASE_URL") ?: ""
            val onlineCardPaymentMethod = properties.getProperty("ONLINE_CARD_PAYMENT_METHOD_ID") ?: ""

            val webClientId = properties.getProperty("WEB_CLIENT_ID") ?: ""
            val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: ""
            val currencyKey = properties.getProperty("CURRENCY_KEY") ?: ""

            buildConfigField("String", "ADMIN_URL", "\"${properties.getProperty("ADMIN_URL")}\"")
            buildConfigField("String", "STOREFRONT_URL", "\"${properties.getProperty("STOREFRONT_URL")}\"")
            buildConfigField("String", "ADMIN_ACCESS_TOKEN", "\"${properties.getProperty("ADMIN_ACCESS_TOKEN")}\"")
            buildConfigField("String", "STOREFRONT_ACCESS_TOKEN", "\"${properties.getProperty("STOREFRONT_ACCESS_TOKEN")}\"")
            buildConfigField("String", "PUBLIC_KEY", "\"${properties.getProperty("PUBLIC_KEY")}\"")
            buildConfigField("String", "SECRET_KEY", "\"${properties.getProperty("SECRET_KEY")}\"")
            buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            buildConfigField("String", "ONLINE_CARD_PAYMENT_METHOD_ID", "\"${properties.getProperty("ONLINE_CARD_PAYMENT_METHOD_ID")}\"")
            buildConfigField("String", "WEB_CLIENT_ID", "\"${properties.getProperty("WEB_CLIENT_ID")}\"")
            buildConfigField("String", "MAPS_API_KEY", "\"${properties.getProperty("MAPS_API_KEY")}\"")
            buildConfigField("String", "CURRENCY_KEY", "\"${properties.getProperty("CURRENCY_KEY")}\"")
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
        buildConfig = true
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
    implementation(libs.androidx.runtime.livedata)
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


    implementation (libs.gson)

    //slider
    implementation(libs.androidx.material)

    implementation (libs.androidx.foundation)


    // Dependencies for local unit tests
    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.junit)
    androidTestImplementation (libs.androidx.espresso.core)

    //MockK
    testImplementation (libs.mockk.android)
    testImplementation (libs.mockk.agent)


    //kotlinx-coroutines
    val coroutinesVersion = "1.10.1"
    implementation (libs.kotlinx.coroutines.android)
    testImplementation (libs.kotlinx.coroutines.test)
    androidTestImplementation (libs.jetbrains.kotlinx.coroutines.test)

    // InstantTaskExecutorRule
    testImplementation (libs.androidx.core.testing)
    androidTestImplementation (libs.androidx.core.testing)

    testImplementation(libs.turbine)

    // paymob

//    implementation(libs.paymob.sdk)
//    implementation("com.paymob.sdk:Paymob-SDK:1.6.7")

    implementation (libs.lottie.compose)
    testImplementation(kotlin("test"))

    implementation(libs.androidx.core.splashscreen)

}