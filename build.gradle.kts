plugins {
    `java-library`
    id("com.gradleup.shadow") version("9.3.1")
    id("xyz.jpenilla.run-paper") version("3.0.2")
}

group = "org.lushplugins"
version = "2.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.lushplugins.org/snapshots/") // LushLib, PlaceholderHandler
    maven("https://repo.fancyplugins.de/releases/") // FancyHolograms
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly("com.mysql:mysql-connector-j:9.6.0")

    // Soft Dependencies
    compileOnly("de.oliver:FancyHolograms:2.9.1")
    compileOnly("me.clip:placeholderapi:2.11.7")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.85")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.14")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.14")
    implementation("org.lushplugins:PlaceholderHandler:1.0.0-alpha8")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    registerFeature("optional") {
        usingSourceSet(sourceSets["main"])
    }

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        relocate("org.lushplugins.lushlib", "org.lushplugins.itempools.libraries.lushlib")

        minimize()

        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        filesMatching("plugin.yml") {
            expand(project.properties)
        }

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }

    runServer {
        minecraftVersion("1.21.11")

        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
            modrinth("viaversion", "5.7.1")
            modrinth("viabackwards", "5.7.1")
        }
    }
}
