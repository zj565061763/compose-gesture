plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  `maven-publish`
}

val libGroupId = "com.sd.lib.android"
val libArtifactId = "compose-gesture"
val libVersion = "1.4.2"

android {
  namespace = "com.sd.lib.compose.gesture"
  compileSdk = libs.versions.androidCompileSdk.get().toInt()
  defaultConfig {
    minSdk = 21
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs += "-module-name=$libGroupId.$libArtifactId"
  }

  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.foundation)
}

publishing {
  publications {
    create<MavenPublication>("release") {
      groupId = libGroupId
      artifactId = libArtifactId
      version = libVersion

      afterEvaluate {
        from(components["release"])
      }
    }
  }
}