plugins {
    id("java")
    id("idea")
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "6.6.1"
    id("net.kyori.blossom") version "1.3.1"
}

val baseGroup: String by project
val mcVersion: String by project
val version: String by project
val mixinGroup = "$baseGroup.mixins"
val modid: String by project
val modName: String by project

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

blossom {
    replaceToken("@VERSION@", version)
    replaceToken("@MODNAME@", modName)
    replaceToken("@MODID@", modid)
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

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set(modid)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
        this["ModSide"] = "CLIENT"

        // If you don't want mixins, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.$modid.json"
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("modName", modName)
    inputs.property("mixinGroup", mixinGroup)

    filesMatching(listOf("mcmod.info", "mixins.$modid.json")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "$baseGroup.deps.$name")
}

tasks.assemble.get().dependsOn(tasks.remapJar)