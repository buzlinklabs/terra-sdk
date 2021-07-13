import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.4.32"
    id("maven-publish")
}

val artifactName = "terra-sdk"
val artifactGroup = "money.terra"
val artifactVersion = "0.3.11"
group = artifactGroup
version = artifactVersion

repositories {
    mavenCentral()
    maven("https://jadekim.jfrog.io/artifactory/maven/")
}

kotlin {
    metadata {
        mavenPublication {
            artifactId = "$artifactName-common"
        }
    }
    jvm {
        withJava()
        mavenPublication {
            artifactId = "$artifactName-jvm"
        }
    }

    sourceSets {
        val ktorClientVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation("kr.jadekim:common-util-common:1.1.8")

                implementation("io.ktor:ktor-client-core:$ktorClientVersion")
                implementation("io.ktor:ktor-client-json:$ktorClientVersion")
                implementation("io.ktor:ktor-client-logging:$ktorClientVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation("kr.jadekim:common-util:1.1.8")

                implementation("org.web3j:crypto:5.0.0")

                implementation("io.ktor:ktor-client-okhttp:$ktorClientVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorClientVersion")
                implementation("io.ktor:ktor-client-jackson:$ktorClientVersion")

                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")
            }

            tasks.withType<KotlinCompile> {
                val jvmTarget: String by project

                kotlinOptions.jvmTarget = jvmTarget
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }

    publishing {
        repositories {
            maven {
                val jfrogUsername: String by project
                val jfrogPassword: String by project

                setUrl("https://jadekim.jfrog.io/artifactory/maven/")

                credentials {
                    username = jfrogUsername
                    password = jfrogPassword
                }
            }
        }
    }
}