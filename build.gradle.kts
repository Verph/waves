import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("java")
    id("idea")
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

val minecraftVersion: String = "1.20.1"
// Don't bump this unless completely necessary - this is the NeoForge + Forge compatible version
// In future we probably want to track NeoForge versions, especially post-1.20 breaking change window
val forgeVersion: String = "47.1.3"
val mixinVersion: String = "0.8.5"
val modVersion: String = System.getenv("VERSION") ?: "1.2"

val modId: String = "waves"

base {
    archivesName.set("Waves-$minecraftVersion")
    group = "waves"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
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

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")
    implementation(fg.deobf("curse.maven:terrafirmacraft-302973:5571484"))
    implementation(fg.deobf("lotr:lotr:Renewed-5.5"))
    implementation(fg.deobf("aether_ii:aether_ii:0.0.1"))
    implementation(fg.deobf("curse.maven:oculus-581495:5299671"))
    implementation(fg.deobf("curse.maven:aether-255308:5302178"))

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
    }

}

minecraft {
    mappings("parchment", "2023.09.03-1.20.1")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {
            args("-mixin.config=$modId.mixins.json")

            property("forge.logging.console.level", "debug")

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            jvmArg("-XX:+AllowEnhancedClassRedefinition")

            mods.create(modId) {
                source(sourceSets.main.get())
            }
        }

        register("client") {
            workingDirectory(project.file("run/client"))
        }

        register("server") {
            workingDirectory(project.file("run/server"))

            arg("--nogui")
        }

    }
}

mixin {
    add(sourceSets.main.get(), "waves.refmap.json")
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
            attributes["MixinConfigs"] = "$modId.mixins.json"
        }
    }
}
