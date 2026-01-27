plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version("9.3.1")
}

group = "org.lushplugins"
version = "1.0.3"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.fancyplugins.de/releases/") // FancyHolograms
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://mvn-repo.arim.space/lesser-gpl3/") // MorePaperLib
    maven("https://repo.lushplugins.org/snapshots/") // LushLib
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly("com.mysql:mysql-connector-j:9.5.0")

    // Soft Dependencies
    compileOnly("de.oliver:FancyHolograms:2.9.1")
    compileOnly("me.clip:placeholderapi:2.11.7")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.84")
    implementation("com.zaxxer:HikariCP:7.0.2")
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
}

publishing {
    repositories {
        maven {
            name = "lushReleases"
            url = uri("https://repo.lushplugins.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }

        maven {
            name = "lushSnapshots"
            url = uri("https://repo.lushplugins.org/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
            from(project.components["java"])
        }
    }
}
