import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.11"
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC3")
}

group = "ma.dalfre"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.163")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "rw-ddb"
            packageVersion = "1.0.0"
            windows {
                console = false
                menuGroup = "RW-DDB"
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/rw-ddb.ico"))
            }
            linux {
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/rw-ddb.png"))
            }
            macOS {
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/rw-ddb.png"))
            }
        }
    }
}
