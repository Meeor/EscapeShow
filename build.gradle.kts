plugins {
    kotlin("jvm") version "1.9.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "kr.rion.plugin"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.onarandombox.com/content/groups/public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("net.kyori:adventure-api:4.17.0")
    compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.12")


    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

}