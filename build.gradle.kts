plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "ai.sahaj.gurukul"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass = "ai.sahaj.gurukul.MainKt"
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}