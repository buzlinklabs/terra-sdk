import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "terra-sdk"
val artifactGroup = "money.terra"
val artifactVersion = "0.2.0"
group = artifactGroup
version = artifactVersion

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/jdekim43/maven")
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
            artifactId = artifactName
        }
    }

    sourceSets {
        val ktorClientVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation("kr.jadekim:common-util-common:1.1.0")

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

                implementation("kr.jadekim:common-util:1.1.0")

                implementation("org.web3j:crypto:4.5.17")

                implementation("io.ktor:ktor-client-okhttp:$ktorClientVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorClientVersion")
                implementation("io.ktor:ktor-client-jackson:$ktorClientVersion")
                implementation("io.ktor:ktor-client-logging-jvm:$ktorClientVersion")

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
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")

    publish = true

    setPublications("jvm", "metadata")

    pkg.apply {
        userOrg = "terra-money"
        repo = "maven"
        name = "terra-sdk"
        setLicenses("Apache2.0")
        setLabels("kotlin")
        vcsUrl = "https://github.com/buzlinklabs/terra-sdk.git"
        version.apply {
            name = artifactVersion
            released = Date().toString()
        }
    }
}