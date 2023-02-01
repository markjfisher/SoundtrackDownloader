import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "fish.soundtrackdl"
version = "1.0-SNAPSHOT"

val jsoupVersion: String by project
val unirestJavaVersion: String by project
val argParserVersion: String by project


repositories {
    mavenCentral()
}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "7.6"
        distributionType = Wrapper.DistributionType.ALL
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jsoup:jsoup:$jsoupVersion")
//    implementation("org.apache.httpcomponents:httpclient-cache:$cacheVersion")
    implementation("com.konghq:unirest-java:$unirestJavaVersion") {
//        exclude(group = "org.apache.httpcomponents", module = "httpclient-cache")
    }
    implementation("com.xenomachina:kotlin-argparser:$argParserVersion")

    testImplementation(kotlin("test"))
}

tasks {
    "build" {
        dependsOn(shadowJar)
    }

    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

}

application {
    mainClass.set("fish.stdl.MainKt")
}