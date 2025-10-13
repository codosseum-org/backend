import de.undercouch.gradle.tasks.download.Download


// SPDX-FileCopyrightText: 2023 Alex Wood
// SPDX-License-Identifier: AGPL-3.0-or-later
plugins {
    id("io.micronaut.application") version "4.5.5"
    id("io.micronaut.aot") version "4.5.5"
    id("io.micronaut.openapi") version "4.5.5"
    id("groovy")
    id("de.undercouch.download") version "5.6.0"
    checkstyle
}

version = "0.1.0"
group = "org.developerden"
val challengesOpenApiSpec: Provider<RegularFile> = layout.buildDirectory.file("openapi/challenges-openapi.yaml")

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    implementation("io.micronaut.openapi:micronaut-openapi-annotations")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.soabase.record-builder:record-builder-processor:49")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.soabase.record-builder:record-builder-core:49")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-http-client")
    implementation("com.github.kkuegler:human-readable-ids-java:0.1.1")
    implementation("com.networknt:json-schema-validator:1.0.86")
    implementation("org.spdx:java-spdx-library:(,2.0]") {
        exclude("org.apache.logging.log4j")
    }
    implementation("org.mapstruct:mapstruct:1.6.3")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml")


    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.micronaut.test:micronaut-test-spock")
    testImplementation(platform("org.spockframework:spock-bom:+"))
    testImplementation("org.spockframework:spock-core")

}


application {
    mainClass.set("org.developerden.codosseum.Application")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

checkstyle {
    toolVersion = "11.1.0"
    configFile = configDirectory.file("google_checks.xml").get().asFile
    sourceSets = listOf(project.sourceSets.main.get())
}

tasks.withType<Checkstyle> {
    exclude {
        it.file.path.contains("generated/openapi") // TODO: this could be slow?
    }
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
        client(
            "challenges-service",
            challengesOpenApiSpec
        ) {
            apiPackageName = "org.developerden.codosseum.challenges.client.api"
            modelPackageName = "org.developerden.codosseum.challenges.client.model"
            useOptional = true
        }
    }
}


val downloadChallengesServiceOpenApi by tasks.registering(Download::class) {
    src("https://raw.githubusercontent.com/codosseum-org/challenges-service/refs/heads/openapi/openapi.yaml")
    dest(challengesOpenApiSpec)
    overwrite(true)
    onlyIfModified(false)
}

tasks.named("generateChallenges-serviceOpenApiModels") { // task created by micronaut-openapi plugin
    dependsOn(downloadChallengesServiceOpenApi)
}

tasks.named("generateChallenges-serviceOpenApiApis") { // task created by micronaut-openapi plugin
    dependsOn(downloadChallengesServiceOpenApi)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    systemProperties["junit.jupiter.execution.parallel.enabled"] = true
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}
