buildscript {
    val TWITCH_AUTHORIZATION by extra("bx8cpl4zq1p0lp9dmbxwdqp3y2c5pa")
    val TWITCH_CLIENT_ID by extra("18co1vlea403qe8lltwkesrmlw7hs0")
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("io.gitlab.arturbosch.detekt") version("1.23.1")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}
val TWITCH_AUTHORIZATION by extra("bx8cpl4zq1p0lp9dmbxwdqp3y2c5pa")
val TWITCH_CLIENT_ID by extra("18co1vlea403qe8lltwkesrmlw7hs0")

