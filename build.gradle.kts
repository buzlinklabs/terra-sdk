plugins {
    kotlin("multiplatform") version "1.3.72"
}

group = "money.terra"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/jdekim43/maven")
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        val ktorClientVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation("kr.jadekim:common-util-common:1.1.0")

                implementation("io.ktor:ktor-client-core:$ktorClientVersion")
                implementation("io.ktor:ktor-client-json:$ktorClientVersion")
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

                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")
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