plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.drapps"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.drapps"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.picasso:picasso:2.71828")
//CardView
    implementation("com.google.android.material:material:1.11.0")
//RecycleView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.android.volley:volley:1.2.1")
    //Runtime permissions
    implementation("com.karumi:dexter:6.2.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.github.AtifSayings:CircularImageView:1.0.2")
    //bubble bot nav
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
    //PhotoView
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    //excel
    implementation("org.apache.poi:poi:3.17")
    implementation("org.apache.poi:poi-ooxml:3.17")
    implementation("javax.xml.stream:stax-api:1.0")
    implementation("org.apache.xmlbeans:xmlbeans:3.1.0")
    implementation("com.fasterxml:aalto-xml:1.2.2")


}