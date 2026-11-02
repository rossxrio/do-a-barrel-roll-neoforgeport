plugins {
    `maven-publish`
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("me.fallenbreath.yamlang") version "1.5.0"
}

val modId = property("mod.id").toString()
val modVersion = property("mod.version").toString()
val mcVersion = property("mod.mc_version").toString()
val mcDep = property("mod.mc_dep").toString()

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
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$mcVersion+build.${property("deps.yarn_build")}:v2")
        mappings("dev.architectury:yarn-mappings-patch-neoforge:${property("deps.yarn_patch")}")
    })
    "neoForge"("net.neoforged:neoforge:${property("deps.neoforge")}")

    // YACL is optional at runtime; the config screen falls back to a download prompt without it.
    modCompileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge")
    modLocalRuntime("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge")
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
    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "run"
    }

    // `./gradlew runClient -PquickPlay=<world>` boots straight into a singleplayer world (handy for smoke tests).
    findProperty("quickPlay")?.let { world ->
        runConfigs.named("client") { programArgs("--quickPlaySingleplayer", world.toString()) }
    }

    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = false
    }
}

tasks.processResources {
    val map = mapOf("version" to modVersion, "mc" to mcDep)
    inputs.properties(map)
    filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/$modId/lang")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}
