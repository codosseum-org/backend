// SPDX-FileCopyrightText: 2023 Alex Wood
// SPDX-License-Identifier: AGPL-3.0-or-later
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.1"
    id("io.micronaut.aot") version "4.0.1"
    id("io.micronaut.openapi") version "4.0.1"
    checkstyle
}

version = "0.1.0"
group = "org.developerden"

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.soabase.record-builder:record-builder-processor:37")

    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.soabase.record-builder:record-builder-core:37")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-http-client")
    implementation("com.github.kkuegler:human-readable-ids-java:0.1.1")
    implementation("com.networknt:json-schema-validator:1.0.86")
    implementation("org.spdx:java-spdx-library:1.1.7")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml")
}


application {
    mainClass.set("org.developerden.codosseum.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

checkstyle {
    toolVersion = "10.12.2"
    configFile = configDirectory.file("google_checks.xml").get().asFile
    sourceSets = emptySet()
}

tasks {

    dockerBuild {
        images.set(listOf("${System.getenv("DOCKER_IMAGE") ?: project.name}:$project.version"))
    }

    dockerBuildNative {
        images.set(listOf("${System.getenv("DOCKER_IMAGE") ?: project.name}:$project.version"))
    }
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.developerden.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }

    openapi {

    }
}
