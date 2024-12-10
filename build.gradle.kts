plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow") version("8.1.7")
}

group = "org.lushplugins"
version = "1.0.3"

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
    compileOnly("org.spigotmc:spigot:1.21-R0.1-SNAPSHOT")
    compileOnly("de.oliver:FancyHolograms:2.3.0")
    compileOnly("me.clip:placeholderapi:2.11.2")
    implementation("org.lushplugins:LushLib:0.7.7")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.lushplugins.lushlib", "org.lushplugins.itempools.libraries.lushlib")
        relocate("com.mysql", "org.lushplugins.itempools.libraries.mysql")

        minimize {
            exclude(dependency("com.mysql:.*:.*"))
        }

        val folder = System.getenv("pluginFolder")
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