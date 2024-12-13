import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.RunConfigurationContainer
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("eclipse")
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.0"
}

// Project properties
group = "cr0s.warpdrive"
version = "1.0.0" // Update to match your versioning strategy

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.AZUL)
    }
    withSourcesJar()
    withJavadocJar()
}

minecraft {
    mcVersion.set("1.7.10") // Change as per your project's Minecraft version
    username.set("Developer")
    injectedTags.put("VERSION", project.version)

    extraRunJvmArguments.add("-ea:${project.group}")

    groupsToExcludeFromAutoReobfMapping.addAll("com.diffplug", "com.diffplug.durian", "net.industrial-craft")
}

// Process resources for mcmod.info
tasks.named<ProcessResources>("processResources") {
    val projVersion = project.version.toString()
    inputs.property("version", projVersion)

    filesMatching("mcmod.info") {
        expand(mapOf("modVersion" to projVersion))
    }
}

// Create a new dependency type for runtime-only dependencies that don't get included in the maven publication
val runtimeOnlyNonPublishable: Configuration by configurations.creating {
    description = "Runtime only dependencies that are not published alongside the jar"
    isCanBeConsumed = false
    isCanBeResolved = false
}

listOf(configurations.runtimeClasspath, configurations.testRuntimeClasspath).forEach {
    it extendsFrom runtimeOnlyNonPublishable
}

// Repositories for dependencies
repositories {
    maven {
        name = "OvermindDL1 Maven"
        url = uri("https://gregtech.overminddl1.com/")
    }
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
}

dependencies {
    runtimeOnlyNonPublishable("com.github.GTNewHorizons:NotEnoughItems:2.3.39-GTNH:dev")
}

// Publishing to a Maven repository
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://nexus.gtnewhorizons.com/repository/releases/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: "NONE"
                password = System.getenv("MAVEN_PASSWORD") ?: "NONE"
            }
        }
    }
}

// IDE Settings
eclipse {
    classpath {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = true
    }
    project {
        this.withGroovyBuilder {
            "settings" {
                "runConfigurations" {
                    val self = this.delegate as RunConfigurationContainer
                    self.add(Gradle("1. Run Client").apply {
                        setProperty("taskNames", listOf("runClient"))
                    })
                    self.add(Gradle("2. Run Server").apply {
                        setProperty("taskNames", listOf("runServer"))
                    })
                    self.add(Gradle("3. Run Obfuscated Client").apply {
                        setProperty("taskNames", listOf("runObfClient"))
                    })
                    self.add(Gradle("4. Run Obfuscated Server").apply {
                        setProperty("taskNames", listOf("runObfServer"))
                    })
                }
                "compiler" {
                    val self = this.delegate as org.jetbrains.gradle.ext.IdeaCompilerConfiguration
                    afterEvaluate {
                        self.javac.moduleJavacAdditionalOptions = mapOf(
                            (project.name + ".main") to tasks.named<JavaCompile>("compileJava").get().options.compilerArgs.joinToString(" ")
                        )
                    }
                }
            }
        }
    }
}

// Custom task for copying resources
fun createCopyTask(name: String, fromDir: String, intoDir: String) {
    tasks.register<Copy>(name) {
        from(fromDir)
        into(intoDir)
    }
}

createCopyTask("copyOpenComputersCommons1", "src/main/resources/assets/warpdrive/lua.OpenComputers/common", "build/resources/main/assets/warpdrive/lua.OpenComputers/warpdriveAccelerator")
createCopyTask("copyOpenComputersCommons2", "src/main/resources/assets/warpdrive/lua.OpenComputers/common", "build/resources/main/assets/warpdrive/lua.OpenComputers/warpdriveEnanReactorCore")
createCopyTask("copyOpenComputersCommons3", "src/main/resources/assets/warpdrive/lua.OpenComputers/common", "build/resources/main/assets/warpdrive/lua.OpenComputers/warpdriveShipController")
createCopyTask("copyOpenComputersCommons4", "src/main/resources/assets/warpdrive/lua.OpenComputers/common", "build/resources/main/assets/warpdrive/lua.OpenComputers/warpdriveTransporterCore")
createCopyTask("copyOpenComputersCommons5", "src/main/resources/assets/warpdrive/lua.OpenComputers/common", "build/resources/main/assets/warpdrive/lua.OpenComputers/warpdriveWeaponController")

tasks.named<Jar>("jar") {
    dependsOn("copyOpenComputersCommons1", "copyOpenComputersCommons2", "copyOpenComputersCommons3", "copyOpenComputersCommons4", "copyOpenComputersCommons5")

    manifest {
        attributes("FMLAT" to "WarpDrive_at.cfg")
        attributes(
            "FMLCorePlugin" to "cr0s.warpdrive.core.FMLLoadingPlugin",
            "FMLCorePluginContainsFMLMod" to true
        )
    }
    classifier = ""
    destinationDirectory.set(file("output"))
}

// Ensure that injectTags task is run before processing IDEA settings
tasks.named("processIdeaSettings").configure {
    dependsOn("injectTags")
}
