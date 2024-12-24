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
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.maxhenkel.de/repository/public") // VoiceChat API와 같은 외부 라이브러리
    maven("https://maven.mohistmc.com/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("net.kyori:adventure-api:4.17.0")
    implementation(files("libs/item-nbt-api-plugin-2.13.2.jar"))
    implementation("com.mohistmc:mohistdev:1.20.1")
    compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.12")
    compileOnly(group = "org.popcraft", name = "chunky-common", version = "1.4.10")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    implementation("de.maxhenkel.voicechat:voicechat-api:2.3.3")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "kr.rion.plugin.Loader"
        )
    }
    from(sourceSets.main.get().output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}