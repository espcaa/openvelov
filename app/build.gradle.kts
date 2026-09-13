import java.util.Properties

val localProps = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}
val hasReleaseKeystore = localProps.getProperty("releaseStoreFile")?.let { rootProject.file(it).exists() } == true &&
    !localProps.getProperty("releaseStorePassword").isNullOrBlank() &&
    !localProps.getProperty("releaseKeyAlias").isNullOrBlank() &&
    !localProps.getProperty("releaseKeyPassword").isNullOrBlank()

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "fish.alice.openvelov"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "fish.alice.openvelov"
        minSdk = 36
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["appAuthRedirectScheme"] = "fish.alice.openvelov"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "VELOV_CLIENT_KEY", "\"${localProps.getProperty("velovClientKey")}\"")
        buildConfigField("String", "VELOV_REFRESH_TOKEN", "\"${localProps.getProperty("velovRefreshToken")}\"")

    }

    signingConfigs {
        create("release") {
            if (hasReleaseKeystore) {
                storeFile = rootProject.file(localProps.getProperty("releaseStoreFile")!!)
                storePassword = localProps.getProperty("releaseStorePassword")
                keyAlias = localProps.getProperty("releaseKeyAlias")
                keyPassword = localProps.getProperty("releaseKeyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.compose.ui.text)
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // nav
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // network
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // auth
    implementation(libs.openid)
    implementation(libs.androidx.datastore.preferences)

    // wizzard
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.maplibre.compose)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // maplibre
    runtimeOnly(libs.maplibre.compose.runtime.vulkan.android)
    implementation(libs.location.runtime.gms)
}