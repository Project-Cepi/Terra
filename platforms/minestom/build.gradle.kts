import com.dfsek.terra.configureCommon
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
}

configureCommon()

group = "com.dfsek.terra"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io/") }
}

dependencies {
    "shadedApi"(project(":common"))
    "shadedImplementation"("com.github.Minestom:Minestom:5eb5f32095")
    "shadedImplementation"("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("net.querz", "com.dfsek.terra.libs.nbt")
}