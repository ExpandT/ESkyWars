import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id ("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.expandt"
version = "1.0-SNAPSHOT"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/**")
        destinationDirectory.set(file("${System.getProperty("user.home")}/Desktop/Server Test/plugins"))
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "protocollib"
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation(kotlin("reflect"))
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("org.expandt.eskywars.intake:intake-core:1.2-SNAPSHOT")
    implementation("org.expandt.eskywars.intake:intake-bukkit:1.2-SNAPSHOT")
    implementation("org.expandt.eskywars:PacketWrapper")
    implementation("org.expandt.eskywars:worldedit-bukkit-7.2.2-dist")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}
