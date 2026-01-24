plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "io.github.bhuyanp.intellij"
version = "2026.1.6"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

val lombokVersion = "1.18.30"

dependencies {
    implementation("com.github.dtmo.jfiglet:jfiglet:1.0.1")
    implementation("org.apache.maven:maven-model:3.9.12")//for pom parsin
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    intellijPlatform {
        intellijIdea("2025.2.4")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            - Auto detection of app version from the build files<br/>
            - App version is available as an option under captions<br/>
            - App version is also available as a variable<br/>
            - Multiple caption bullet styles to choose from including randomization<br/>
            - Several performance improvements 
        """.trimIndent()
    }

    pluginVerification{
        ides{
            recommended()
        }
    }

    signing{
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}
