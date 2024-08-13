import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

val group: String by project
val version: String by project
val platformType: String by project
val platformVersion: String by project

plugins {
    java
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")

    intellijPlatform {
        create(platformType, platformVersion)

        plugin("IdeaVIM:2.15.3")

        pluginVerifier()
        zipSigner()
        instrumentationTools()

        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("junit", "junit", "4.13.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}