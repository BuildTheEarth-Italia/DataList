/*
 * Copyright (c) 2021 Build The Earth Italia
 * This file (build.gradle.kts) and its related project (DataList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Year

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
}

group = "it.bteitalia.datalist"
version = "1.1"
val mainClassName = "$group.DataList"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://ci.ender.zone/plugin/repository/everything/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    //plugin dependencies
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("net.ess3:EssentialsX:2.17.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    //kotlin library
    implementation(kotlin("stdlib-jdk8"))
}

java.targetCompatibility = JavaVersion.VERSION_1_8
java.sourceCompatibility = JavaVersion.VERSION_1_8

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.named<ShadowJar>("shadowJar") {
    // Rimuovo suffisso "all"
    archiveClassifier.set("")

    // Diminuisco la dimensione
    minimize()

    manifest {
        attributes(
            mapOf(
                "Main-Class" to mainClassName,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}


tasks.named<ProcessResources>("processResources") {
    from ("src/${sourceSets.main.name}/resources" ) {
        include("plugin.yml")
        include("LICENSE.txt")

        expand (
            Pair("version", project.version),
            Pair("main", mainClassName),
            Pair("author", "MemoryOfLife"),
            Pair("copyrighter", "Build The Earth Italia"),
            Pair("year", Year.now())
        )
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}