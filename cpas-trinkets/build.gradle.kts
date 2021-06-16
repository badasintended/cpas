repositories {
    maven("https://maven.terraformersmc.com/releases")
    maven("https://ladysnake.jfrog.io/artifactory/mods")
}

dependencies {
    implementation(project(":cpas-api"))
    modImplementation("dev.emi:trinkets:${rootProp["trinkets"]}")
}
