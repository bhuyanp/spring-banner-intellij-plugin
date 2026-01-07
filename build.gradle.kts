plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "io.github.bhuyanp.intellij"
version = "2026.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
val lombokVersion = "1.18.30"
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    implementation("com.github.dtmo.jfiglet:jfiglet:1.0.1")
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
            sinceBuild = "252.25557"
        }

        changeNotes = """
        <h2>First Release</h2>
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
