import me.modmuss50.mpp.ReleaseType

plugins {
    id("fabric-loom") version "1.4.+"
    id("me.modmuss50.mod-publish-plugin") version "0.4.5"
}

val env: Map<String, String> = System.getenv()
version = env["MOD_VERSION"] ?: "local"

allprojects {
    apply(plugin = "fabric-loom")

    version = rootProject.version

    dependencies {
        minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
        mappings("net.fabricmc:yarn:${rootProp["yarn"]}:v2")

        modImplementation("net.fabricmc:fabric-loader:${rootProp["loader"]}")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
    }

    tasks.processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    tasks.jar {
        from(rootProject.file("includes"))
    }
}

subprojects.forEach {
    tasks.remapJar {
        dependsOn(it.tasks.named("remapJar").get())
    }
}

repositories {
    maven("https://maven.shedaniel.me")
}

dependencies {
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")

    afterEvaluate {
        subprojects.forEach {
            implementation(project(path = it.path, configuration = "namedElements"))
            include(it)
        }
    }
}

publishMods {
    file.set(tasks.remapJar.get().archiveFile)
    changelog.set("https://github.com/badasintended/cpas/releases/tag/${project.version}")
    type.set(ReleaseType.of(prop["releaseType"]))
    modLoaders.add("fabric")

    env["CURSEFORGE_API"]?.let { apiKey ->
        curseforge {
            accessToken.set(apiKey)
            projectId.set(prop["cf.projectId"])

            minecraftVersions.addAll(prop["cf.gameVersion"].split(", "))

            prop.ifPresent("cf.require") { requires(*it.split(", ").toTypedArray()) }
            prop.ifPresent("cf.optional") { optional(*it.split(", ").toTypedArray()) }
        }
    }

    env["MODRINTH_TOKEN"]?.let { token ->
        modrinth {
            accessToken.set(token)
            projectId.set(prop["cf.projectId"])

            minecraftVersions.addAll(prop["mr.gameVersion"].split(", "))

            prop.ifPresent("mr.require") { requires(*it.split(", ").toTypedArray()) }
            prop.ifPresent("mr.optional") { optional(*it.split(", ").toTypedArray()) }
        }
    }
}
