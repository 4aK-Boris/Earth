object Dependencies {

    object Compose {

        const val version = "1.3.1"
        private const val constraintVersion = "1.1.0-alpha04"

        const val ui = "androidx.compose.ui:ui:$version"
        const val preview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val material = "androidx.compose.material:material:$version"
        const val junit = "androidx.compose.ui:ui-test-junit4:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val manifest = "androidx.compose.ui:ui-test-manifest:$version"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:$constraintVersion"
    }

    object Tests {
        const val junit = "junit:junit:4.13.2"
        const val androidJunit = "androidx.test.ext:junit:1.1.4"
        const val espresso = "androidx.test.espresso:espresso-core:3.5.0"
    }

    object Android {
        const val core = "androidx.core:core-ktx:1.9.0"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
        const val activity = "androidx.activity:activity-compose:1.6.1"
    }

    object Plugins {

        const val androidVersion = "7.3.1"
        const val kotlinVersion = "1.7.10"

        const val application = "com.android.application"
        const val kotlin = "org.jetbrains.kotlin.android"
        const val library = "com.android.library"
    }

    object Coroutines {

        private const val version = "1.3.9"

        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object Koin {

        private const val version = "3.3.0"

        const val koin = "io.insert-koin:koin-android:$version"
    }
}