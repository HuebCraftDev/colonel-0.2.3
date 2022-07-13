import ca.stellardrift.build.configurate.ConfigFormats
import ca.stellardrift.build.configurate.transformations.convertFormat

plugins {
    val opinionatedVersion = "5.0.1"
    val indraVersion = "2.1.1"
    id("ca.stellardrift.configurate-transformations") version opinionatedVersion
    id("net.kyori.indra.publishing") version indraVersion
    id("fabric-loom") version "0.12-SNAPSHOT"
}

group = "de.huebcraft"
version = "0.2.3"
description = "An extension to Minecraft's command system to allow client-optional custom argument types modified by and for HuebCraft"

repositories {
    maven("https://repo.stellardrift.ca/repository/stable/") {
        name = "stellardrift"
        mavenContent { releasesOnly() }
    }

    maven("https://repo.stellardrift.ca/repository/snapshots/") {
        name = "stellardrift"
        mavenContent { snapshotsOnly() }
    }
}
/*
indra {
    github("HuebCraftDev", "colonel-0.2.3") {

    }

    configurePublications {
        publishReleasesTo("huebcraft", "https://repo.huebcraft.net/releases/")
        pom {
            developers {
                developer {
                    id.set("zml")
                    name.set("zml")
                    timezone.set("America/Vancouver")
                    email.set("zml [at] stellardrift . ca")
                }
            }
        }
    }

}*/

publishing {
    publications {
        create<MavenPublication>("colonel") {
            artifactId = "colonel"
            groupId = project.group as String?
            version = project.version as String?
            from(components["java"])
            pom {
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("zml")
                        name.set("zml")
                        timezone.set("America/Vancouver")
                        email.set("zml [at] stellardrift . ca")
                    }
                }
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
    repositories {
        maven {
            name = "HuebCraftRelease"
            url = uri("https://repo.huebcraft.net/releases")
            credentials {
                username =
                    if (project.hasProperty("mavenUser"))
                        project.property("mavenUser") as String
                    else
                        System.getenv("USERNAME")
                password =
                    if (project.hasProperty("mavenPassword"))
                        project.property("mavenPassword") as String
                    else
                        System.getenv("TOKEN")
            }
        }
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
val versionMinecraft: String by project
val versionYarn: String by project
val versionLoader: String by project
val versionFabricApi: String by project

dependencies {
    minecraft("com.mojang:minecraft:$versionMinecraft")
    mappings("net.fabricmc:yarn:${versionYarn}:v2")
    modImplementation("net.fabricmc:fabric-loader:$versionLoader")

    annotationProcessor("com.google.auto.value:auto-value:1.8.2")
    compileOnly("com.google.auto.value:auto-value-annotations:1.8.2")
    modApi("org.checkerframework:checker-qual:3.17.0")

    // fapi -- optional
    modImplementation("net.fabricmc.fabric-api:fabric-api:$versionFabricApi")
}

tasks.withType(ProcessResources::class).configureEach {
    val props = mapOf("project" to project)

    filesMatching("*.mixins.json") {
        expand(props)
    }
    filesMatching("fabric.mod.yml") {
        expand(props)
        convertFormat(ConfigFormats.YAML, ConfigFormats.JSON)
        name = "fabric.mod.json"
    }
}