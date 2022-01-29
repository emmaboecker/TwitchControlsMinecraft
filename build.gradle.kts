plugins {
	id("fabric-loom") version "0.10-SNAPSHOT"
	val kotlinVersion: String by System.getProperties()
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.serialization") version kotlinVersion
}

val sourceCompatibility = JavaVersion.VERSION_17
val targetCompatibility = JavaVersion.VERSION_17

val archives_base_name: String by project
val mod_version: String by project
val maven_group: String by project

repositories {
	mavenCentral()
}

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project

val fabric_version: String by project
val fabric_kotlin_version: String by project
val fabrikmc_version: String by project

dependencies {
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings("net.fabricmc:yarn:$yarn_mappings:v2")

	implementation("com.github.twitch4j:twitch4j:1.8.0")

	modImplementation("net.fabricmc:fabric-loader:$loader_version")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

	modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
	modImplementation("net.axay:fabrikmc-core:$fabrikmc_version")
	modImplementation("net.axay:fabrikmc-commands:$fabrikmc_version")
	modImplementation("net.axay:fabrikmc-igui:$fabrikmc_version")
	modImplementation("net.axay:fabrikmc-persistence:$fabrikmc_version")
}

kotlin {
	jvmToolchain {
		(this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
	}
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"

		options.release.set(17)
	}


	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			jvmTarget = "17"
			freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
		}
	}

	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			this.expand("version" to project.version)
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${archives_base_name}" }
		}
	}
}
