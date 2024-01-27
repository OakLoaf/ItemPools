plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.dave"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") } // ChatColorHandler
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT")
    implementation("com.github.CoolDCB:ChatColorHandler:v2.5.0")
    implementation(files("libs/PlatyUtils-0.1.0.30.jar"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("me.dave.chatcolorhandler", "me.dave.itempools.libraries.chatcolor")
        relocate("me.dave.platyutils", "me.dave.itempools.libraries.platyutils")

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