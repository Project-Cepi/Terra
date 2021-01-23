import com.dfsek.terra.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
}

configureCommon()

group = "com.dfsek.terra"

repositories {
    // Use jcenter for resolving dependencies.
    jcenter()

    // Use mavenCentral
    maven(url = "https://repo1.maven.org/maven2/")
    maven(url = "http://repo.spongepowered.org/maven")
    maven(url = "https://libraries.minecraft.net")
    maven(url = "https://jitpack.io")
    maven(url = "https://jcenter.bintray.com/")
}

dependencies {
    "shadedApi"(project(":common"))
    "shadedImplementation"("com.github.Minestom:Minestom:09d8f74b07")
    "shadedImplementation"("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("net.querz", "com.dfsek.terra.libs.nbt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}