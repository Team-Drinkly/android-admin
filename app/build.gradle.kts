import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val kakaoNativeKey = properties.getProperty("kakao_app_key")

android {
    namespace = "com.project.drinkly_admin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.drinkly_admin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SERVER_URL", "\"${properties["server_url"]}\"")
        buildConfigField("String", "PRESIGNED_URL", "\"${properties["presigned_url"]}\"")
        buildConfigField("String", "PASS_URL", "\"${properties["pass_url"]}\"")
        buildConfigField("String", "DATA_API_URL", "\"${properties["data_api_url"]}\"")

        buildConfigField("String", "KAKAO_APP_KEY", "\"${properties["kakao_key"]}\"")
        buildConfigField("String", "KAKAO_APP_KEY", "\"${properties["kakao_key"]}\"")
        buildConfigField("String", "OPEN_DATA_KEY", "\"${properties["open_data_key"]}\"")

        manifestPlaceholders["kakao_native_key"] = kakaoNativeKey
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dataBinding {
        enable = true
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.20.6")

    // api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 변환
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // OkHttp 라이브러리
    implementation("com.squareup.okhttp3:logging-interceptor:3.11.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    // dot indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // calendar
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
}