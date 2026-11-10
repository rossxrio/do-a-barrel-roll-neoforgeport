plugins {
    `maven-publish`
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("me.fallenbreath.yamlang") version "1.5.0"
}

val modId = property("mod.id").toString()
val modVersion = property("mod.version").toString()
val mcVersion = property("mod.mc_version").toString()
val mcDep = property("mod.mc_dep").toString()
val neoforgeDep = property("deps.neoforge_dep").toString()
val mixinExtras = property("deps.mixinextras").toString()

version = "$modVersion+$mcVersion"
group = property("mod.group").toString()
base { archivesName.set("$modId-neoforge") }

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.quiltmc.org/repository/release/")
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:${property("deps.yarn")}:v2")
    // NeoForge for 1.20.1 is published as the legacy Forge artifact.
    "forge"("net.neoforged:forge:$mcVersion-${property("deps.neoforge")}")

    // Forge 47 does not ship MixinExtras, so it is bundled as a jar-in-jar.
    compileOnly("io.github.llamalad7:mixinextras-common:$mixinExtras")
    annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtras")
    implementation("io.github.llamalad7:mixinextras-forge:$mixinExtras")
    include("io.github.llamalad7:mixinextras-forge:$mixinExtras")

    // YACL is optional at runtime; the config screen falls back to a download prompt without it.
    modCompileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")
    modLocalRuntime("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")
    // YACL jar-in-jars these in production; the dev runtime needs them explicitly.
    listOf(
        "org.quiltmc.parsers:json:0.2.1",
        "org.quiltmc.parsers:gson:0.2.1",
        "com.twelvemonkeys.imageio:imageio-core:3.12.0",
        "com.twelvemonkeys.imageio:imageio-webp:3.12.0",
        "com.twelvemonkeys.imageio:imageio-metadata:3.12.0",
        "com.twelvemonkeys.common:common-lang:3.12.0",
        "com.twelvemonkeys.common:common-io:3.12.0",
        "com.twelvemonkeys.common:common-image:3.12.0",
    ).forEach { "forgeRuntimeLibrary"(it) }
}

loom {
    forge {
        mixinConfig("$modId.mixins.json")
    }

    // No refmap: Loom's tiny-remapper mixin extension rewrites the mixin targets to SRG names in remapJar.
    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = false
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "run"
    }

    // `./gradlew runClient -PquickPlay=<world>` boots straight into a singleplayer world (handy for smoke tests).
    findProperty("quickPlay")?.let { world ->
        runConfigs.named("client") { programArgs("--quickPlaySingleplayer", world.toString()) }
    }
}

tasks.processResources {
    val map = mapOf("version" to modVersion, "mc" to mcDep, "neoforge" to neoforgeDep)
    inputs.properties(map)
    filesMatching("META-INF/mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/$modId/lang")
}

java {
    withSourcesJar()
    // Loom itself needs a Java 21 Gradle JVM, but the mod targets (and the dev runs use) Java 17 like Minecraft 1.20.1.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}
