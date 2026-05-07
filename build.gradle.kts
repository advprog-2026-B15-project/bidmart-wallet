

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id ("org.sonarqube") version "5.1.0.4882"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "wallet"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:10.17.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sonarqube {
    properties {
        property("sonar.projectKey", "advprog-2026-B15-project_bidmart-wallet")
        property("sonar.organization", "advprog-2026-b15-project")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.gradle.skipCompile", "true")
    }
}

checkstyle {
    toolVersion = "10.12.5"
}

tasks.withType<Checkstyle> {
    ignoreFailures = false
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}