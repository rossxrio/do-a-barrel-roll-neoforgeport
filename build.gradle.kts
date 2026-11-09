plugins {
    `java-library`
    id("net.neoforged.moddev") version "2.0.148"
    id("me.fallenbreath.yamlang") version "1.5.0"
}

val modId = property("mod.id").toString()
val modVersion = property("mod.version").toString()
val mcVersion = property("mod.mc_version").toString()
val mcDep = property("mod.mc_dep").toString()
val neoForgeDep = property("mod.neoforge_dep").toString()
val javaVersion = property("deps.java").toString().toInt()

version = "$modVersion+$mcVersion"
group = property("mod.group").toString()
base { archivesName.set("$modId-neoforge") }

repositories {
    maven("https://maven.isxander.dev/releases") {
        content { includeGroup("dev.isxander") }
    }
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
}

neoForge {
    version = property("deps.neoforge").toString()

    runs {
        configureEach {
            jvmArgument("-Dmixin.debug.export=true")
            logLevel.set(org.slf4j.event.Level.INFO)
        }
        create("client") {
            client()
            gameDirectory.set(file("run"))
            // `./gradlew runClient -PquickPlay=<world>` boots straight into a singleplayer world.
            findProperty("quickPlay")?.let { programArguments.addAll("--quickPlaySingleplayer", it.toString()) }
        }
        create("server") {
            server()
            gameDirectory.set(file("run/server"))
            programArgument("--nogui")
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// YACL is an optional dependency: without it the config screen is replaced by a notice screen
// and the JSON config file keeps working. Pass -PnoYacl to run the dev client without it.
val yacl = "dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge"
val localRuntime: Configuration by configurations.creating
configurations.runtimeClasspath { extendsFrom(localRuntime) }

dependencies {
    compileOnly(yacl) { isTransitive = false }
    if (!project.hasProperty("noYacl")) {
        localRuntime(yacl)
    }
}

tasks.processResources {
    val map = mapOf(
        "version" to modVersion,
        "mc" to mcDep,
        "neoforge" to neoForgeDep,
    )
    inputs.properties(map)
    filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/$modId/lang")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.jar {
    from("LICENSE") { rename { "${it}_$modId" } }
}
