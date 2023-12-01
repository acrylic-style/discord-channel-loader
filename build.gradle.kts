plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "xyz.acrylicstyle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kord:kord-core:0.12.0")
    implementation("org.slf4j:slf4j-simple:2.0.1")
}

tasks {
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "xyz.acrylicstyle.discordchannelloader.MainKt",
            )
        }
        archiveFileName.set("DiscordChannelLoader.jar")
    }
}

kotlin {
    jvmToolchain(8)
}
