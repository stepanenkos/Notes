plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}


apply(plugin = "kotlin-parcelize")

android {
    compileSdk = 31
    buildToolsVersion = "30.0.2"

    kapt {
        correctErrorTypes = true
        useBuildCache = false
    }

    packagingOptions {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/*.kotlin_module")
        }

        buildTypes {
            debug {
                isMinifyEnabled = false
            }
        }
    }

    defaultConfig {
        applicationId = "kz.stepanenkos.notes"
        minSdk = 21
        targetSdk = 31
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
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.2")

//Firebase
    implementation (platform("com.google.firebase:firebase-bom:28.4.1"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

//Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

//Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0-alpha10")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0-alpha10")

    implementation("androidx.fragment:fragment-ktx:1.4.0-alpha10")

    implementation("com.jakewharton.threetenabp:threetenabp:1.3.0")

//Koin
    /*implementation ("io.insert-koin:koin-androidx-viewmodel:2.2.3")*/
    implementation("io.insert-koin:koin-android:3.1.2")

//Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")

    implementation("androidx.preference:preference-ktx:1.1.1")

//ViewPump
    implementation("io.github.inflationx:viewpump:2.0.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}


