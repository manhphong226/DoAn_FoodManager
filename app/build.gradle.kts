plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.edu.stu.nguyenmanhphong_dh52301513"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "vn.edu.stu.nguyenmanhphong_dh52301513"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.osmdroid.android)
}
