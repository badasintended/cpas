repositories {
    maven("https://maven.shedaniel.me")
}

dependencies {
    implementation(project(":cpas-api"))

    modApi("net.fabricmc.fabric-api:fabric-api:${rootProp["fabric"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
}