plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.dave"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") } // Spigot
    maven { url = uri("https://repo.fancyplugins.de/releases/")} // FancyHolograms
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")} // PlaceholderAPI
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") } // MorePaperLib
    maven { url = uri("https://repo.smrt-1.com/releases/") } // LushLib
    maven { url = uri("https://repo.smrt-1.com/snapshots/") } // LushLib
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT")
    compileOnly("de.oliver:FancyHolograms:2.0.6")
    compileOnly("me.clip:placeholderapi:2.11.2")
    implementation("org.enchantedskies:EnchantedStorage:3.0.0")
    implementation("me.dave:LushLib:0.1.2.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("me.dave.lushlib", "me.dave.itempools.libraries.lushlib")

        minimize()

        val folder = System.getenv("pluginFolder_1-20")
        if (folder != null) destinationDirectory.set(file(folder))
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        expand(project.properties)

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}