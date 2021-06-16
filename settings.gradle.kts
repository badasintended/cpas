pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        gradlePluginPortal()
    }
}

include("cpas-api")
include("cpas-runtime")
include("cpas-trinkets")

rootProject.name = "cpas"
