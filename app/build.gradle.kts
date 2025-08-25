plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.sd.demo.compose_gesture"
  compileSdk = libs.versions.androidCompileSdk.get().toInt()
  defaultConfig {
    targetSdk = libs.versions.androidCompileSdk.get().toInt()
    minSdk = 21
    applicationId = "com.sd.demo.compose_gesture"
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

  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
}

kotlin {
  jvmToolchain(8)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  testImplementation(composeBom)
  androidTestImplementation(composeBom)

  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.viewmodelCompose)
  implementation(libs.androidx.lifecycle.runtimeCompose)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)

  implementation(project(":lib"))
}