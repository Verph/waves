import groovy.json.JsonSlurper

plugins {
    id("net.neoforged.moddev") version "2.0.1-beta"
    id("com.almostreliable.almostgradle") version "1.0.+"
}

val minecraftVersion: String = "1.21"
val neoForgeVersion: String = "21.0.167"
val parchmentVersion: String = "2024.07.07"
val parchmentMinecraftVersion: String = "1.21"
val modJavaVersion: String = "21"
val modVersion: String = System.getenv("VERSION") ?: "1.2"
val modId: String = "waves"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modId,
        "modVersion" to modVersion
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("Waves-$minecraftVersion")
    group = modId
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://maven.fabricmc.net")
    maven(url = "https://dvs1.progwml6.com/files/maven/") // JEI
    maven(url = "https://modmaven.k-4u.nl") // Mirror for JEI
    maven(url = "https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    flatDir {
        dirs("libs")
    }
}

sourceSets {
    main {
        resources {
            srcDir(generateModMetadata)
        }
    }
}

dependencies {
    implementation("curse.maven:terrafirmacraft-302973:5571484")
    implementation("lotr:lotr:Renewed-5.5")
    implementation("aether_ii:aether_ii:0.0.1")
    implementation("curse.maven:oculus-581495:5299671")
    implementation("curse.maven:aether-255308:5302178")
    implementation("curse.maven:simple-clouds-1121215:5812034")
}

neoForge {
    version.set(neoForgeVersion)
    validateAccessTransformers = true

    parchment {
        minecraftVersion.set(parchmentMinecraftVersion)
        mappingsVersion.set(parchmentVersion)
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    ideSyncTask(generateModMetadata)
}

tasks {
    processResources {}

    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }

    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }
}
