plugins {
    id(Dependencies.Plugins.application)
    id(Dependencies.Plugins.kotlin)
}

android {
    namespace = Settings.namespace
    compileSdk = 33

    defaultConfig {
        applicationId = Settings.namespace
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        jvmTarget = Settings.javaVersion
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.Compose.version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Dependencies.Android.activity)
    implementation(Dependencies.Android.core)
    implementation(Dependencies.Android.lifecycle)
    implementation(Dependencies.Compose.constraintLayout)

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.preview)

    implementation(Dependencies.Coroutines.coroutines)

    implementation(Dependencies.Koin.koin)

    debugImplementation(Dependencies.Compose.manifest)
    debugImplementation(Dependencies.Compose.tooling)

    testImplementation(Dependencies.Tests.junit)

    androidTestImplementation(Dependencies.Tests.androidJunit)
    androidTestImplementation(Dependencies.Tests.espresso)
    androidTestImplementation(Dependencies.Compose.junit)
}