// SPDX-FileCopyrightText: 2023 Alex Wood
// SPDX-License-Identifier: AGPL-3.0-or-later
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.1"
    id("io.micronaut.aot") version "4.0.1"
    id("io.micronaut.openapi") version "4.0.1"
    checkstyle
}

version = "0.1"
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
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.swagger.core.v3:swagger-annotations")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("org.developerden.codosseum.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

checkstyle {
    val archive = configurations.checkstyle.get().resolve().filter {
        it.name.startsWith("checkstyle")
    }
    config = resources.text.fromArchiveEntry(archive, "google_checks.xml")
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
        server(file("openapi.yaml")) {
            apiPackageName = "org.developerden.codosseum.api"
            modelPackageName = "org.developerden.codosseum.model"
            useReactive = false
            useAuth = false
            useBeanValidation = false
        }
    }
}
