import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "me.drman"
version = "1.0"

repositories {
    mavenCentral()
    //Others
    maven {
        name = "papermc-repo"
        url = URI("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = URI("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "CodeMC"
        url = URI("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        url = URI("https://jitpack.io")
    }

    //Plugins with no repos
    flatDir {
        dirs = setOf(File("libs"))
    }

    flatDir {
        dirs = setOf(File("../Spigot_Versions"))
    }
}

dependencies {
    //Jars
    //compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(files("../Spigot_Versions/1.8.8/spigot-1.8.8.jar"))
    //FlatFile
    compileOnly(files("libs/MassiveCore.jar"))
    compileOnly(files("libs/WildStacker-3.6.2.jar"))
    compileOnly(files("libs/SuperiorSkyblock2-1.8.3.jar"))
    compileOnly(files("libs/HolographicDisplays.jar"))
    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    //Plugins
    implementation("io.github.bananapuncher714:nbteditor:7.18.0")
    compileOnly ("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    configurations["runtimeClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    destinationDirectory.set(File("$rootDir/../../plugins"))
}