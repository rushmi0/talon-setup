plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    id("de.jensklingenberg.ktorfit") version "2.5.1"
}

group = "apps.cli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")

    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    val ktorVersion = "3.1.3"
    val ktorfitVersion = "2.5.1"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:${ktorVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("io.ktor:ktor-client-curl:${ktorVersion}")
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:${ktorfitVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation(libs.kotlin.stdlib.common)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val nativeMain by getting
        val nativeTest by getting

    }
}
