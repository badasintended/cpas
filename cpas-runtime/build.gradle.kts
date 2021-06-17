repositories {
    maven("https://maven.shedaniel.me")
}

dependencies {
    implementation(project(":cpas-api"))

    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProp["fabric"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
}