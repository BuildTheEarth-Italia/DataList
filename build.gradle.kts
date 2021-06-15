/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (build.gradle.kts) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
}

buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

group = "it.bteitalia.datalist"
version = "1.2"

apply(plugin = "com.github.johnrengelman.shadow")
apply(plugin = "java")

repositories {
    mavenCentral()
    jcenter()
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}