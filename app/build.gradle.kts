plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "uniba.roadhouse.asilapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "uniba.roadhouse.asilapp"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.firebase:firebase-bom:32.6.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("androidx.core:core-splashscreen:1.0.0-beta01")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation  ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.auth0:java-jwt:4.4.0")
    implementation("org.springframework.security","spring-security-crypto","5.2.2.RELEASE")
    implementation("commons-logging:commons-logging-api:1.1")
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.zxing:javase:3.5.2")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


}