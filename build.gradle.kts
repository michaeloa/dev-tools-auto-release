import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21-2"
    id("com.github.ben-manes.versions") version "0.36.0"
    id("com.adarshr.test-logger") version "2.1.1"
    id("io.qameta.allure") version "2.8.1"
    application
}

val allureVersion = "2.13.8"
val kotestVersion = "4.4.1"
val mockkVersion = "1.10.6"

description = "Implement automated semantic release for gradle, maven and ansible projects."

repositories {
    maven("https://jfrog.elhub.cloud/artifactory/elhub-mvn")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")
    testImplementation("commons-io:commons-io:2.8.0")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-allure-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

application {
    // Define the main class for the application.
    mainClass.set("dev.tools.semantic.release.MainKt")
}

testlogger {
    theme = ThemeType.MOCHA
}

allure {
    version = allureVersion
    autoconfigure = false
    aspectjweaver = true
    useJUnit5 {
        version = allureVersion
    }
    downloadLink = "https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/" +
            "$allureVersion/allure-commandline-$allureVersion.zip"
}
