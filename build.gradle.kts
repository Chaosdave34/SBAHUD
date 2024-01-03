plugins {
    id("java")
    id("idea")
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "6.6.1"
}

val baseGroup: String by project
val mcVersion: String by project
val version: String by project
val mixinGroup = "$baseGroup.mixins"
val modid: String by project

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

loom {
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }

    runConfigs {
        remove(getByName("server"))
    }

    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.$modid.json")
    }

    mixin {
        defaultRefmapName.set("mixins.$modid.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")

    maven("https://repo.essential.gg/repository/maven-public")
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.2")

    modRuntimeOnly("gg.essential:loader-launchwrapper:1.2.2")

    modImplementation("gg.essential:essential-$mcVersion-forge:15744+g24ea70f4b2")
    modImplementation("gg.essential:vigilance-$mcVersion-forge:295")
    modImplementation("gg.essential:elementa-$mcVersion-forge:619")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("mixinGroup", mixinGroup)

    filesMatching(listOf("mcmod.info", "mixins.$modid.json")) {
        expand(inputs.properties)
    }
}