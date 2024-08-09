import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val group: String by project
val version: String by project

plugins {
    java
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.intellij") version "1.17.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit", "junit", "4.13.2")
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

intellij {
    version.set("2024.1.4")
    plugins.set(listOf("IdeaVIM:2.15.2"))
}