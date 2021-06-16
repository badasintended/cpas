@file:Suppress("UnstableApiUsage")

plugins {
    id("fabric-loom").version("0.8-SNAPSHOT")
}

val env: Map<String, String> = System.getenv()
version = env["MOD_VERSION"] ?: "local"

allprojects {
    apply(plugin = "fabric-loom")

    version = rootProject.version

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProp["minecraft"]}")
        "mappings"("net.fabricmc:yarn:${rootProp["yarn"]}:v2")
        "modImplementation"("net.fabricmc:fabric-loader:${rootProp["loader"]}")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
        withSourcesJar()
    }

    afterEvaluate {
        tasks.processResources {
            inputs.property("version", project.version)

            filesMatching("fabric.mod.json") {
                expand("version" to project.version)
            }
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(16)
        }

        tasks.jar {
            from(rootProject.file("includes"))
        }
    }
}

subprojects.forEach {
    tasks.remapJar {
        dependsOn(it.tasks.named("remapJar").get())
    }
}

dependencies {
    afterEvaluate {
        subprojects.forEach {
            implementation(it)
            include(it)
        }
    }
}
