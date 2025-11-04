plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.steiner"
version = "0.2.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.2.0")
    // https://mvnrepository.com/artifact/jakarta.annotation/jakarta.annotation-api
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    testImplementation("ch.qos.logback:logback-classic:1.5.16")

    // https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-core
    testImplementation("org.jetbrains.exposed:exposed-core:0.61.0")
    // https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-kotlin-datetime
    testImplementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.61.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}