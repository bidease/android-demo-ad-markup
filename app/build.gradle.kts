plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.bidease.android.demo.admarkup"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.bidease.android.demo.admarkup"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
        }
    }
}

val apkDir = project.rootProject.layout.projectDirectory.dir("apk")

tasks.register<Copy>("copyReleaseApk") {
    from(layout.buildDirectory.dir("outputs/apk/release"))
    into(apkDir)
    include("*.apk")
    rename { "AndroidDemoAdMarkup.apk" }
}

tasks.register<Copy>("copyDebugApk") {
    from(layout.buildDirectory.dir("outputs/apk/debug"))
    into(apkDir)
    include("*.apk")
    rename { "AndroidDemoAdMarkupDebug.apk" }
}

afterEvaluate {
    tasks.named("assembleRelease") { finalizedBy("copyReleaseApk") }
    tasks.named("assembleDebug") { finalizedBy("copyDebugApk") }
}

configurations.all {
    exclude(group = "com.bidease", module = "bidease-mobile-test-mode")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    implementation(libs.bidease.mobile)
    implementation(libs.admob.adapter)
    implementation(libs.applovin.adapter)
    implementation(libs.levelplay.adapter)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}