repositories {
    maven("https://maven.terraformersmc.com/releases")
    maven("https://maven.ladysnake.org/releases")
}

dependencies {
    implementation(project(":cpas-api"))

    modApi("dev.emi:trinkets:${rootProp["trinkets"]}")
    modApi("dev.onyxstudios.cardinal-components-api:cardinal-components-base:5.0.0")
}
