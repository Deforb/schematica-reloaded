// Allow Mojang's legacy CDN to negotiate TLSv1/1.1 when Gradle starts.
System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3")
System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://maven.architectury.dev/")
        maven("https://repo.sk1er.club/repository/maven-releases/")
        maven("https://repo.essential.gg/repository/maven-releases/")
        maven("https://repo.essential.gg/repository/maven-public/")
        maven("https://repo.essential.gg/repository/maven-snapshots/")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "gg.essential.loom" -> useModule("gg.essential:architectury-loom:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        val localRepo = mavenLocal()
        val bmclMirror = maven("https://bmclapi2.bangbang93.com/maven/") {
            name = "BMCLAPIMirror"
        }

        mavenCentral()
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.essential.gg/repository/maven-releases/")

        exclusiveContent {
            forRepositories(localRepo, bmclMirror)
            filter {
                includeModule("org.lwjgl.lwjgl", "lwjgl")
                includeModule("org.lwjgl.lwjgl", "lwjgl_util")
                includeModule("org.lwjgl.lwjgl", "lwjgl-platform")
                includeModule("net.java.jinput", "jinput")
                includeModule("net.java.jinput", "jinput-platform")
                includeModule("net.java.dev.jna", "jna")
                includeModule("com.paulscode", "codecjorbis")
                includeModule("com.paulscode", "codecwav")
                includeModule("com.paulscode", "libraryjavasound")
                includeModule("com.paulscode", "librarylwjglopenal")
                includeModule("com.paulscode", "soundsystem")
                includeModule("commons-codec", "commons-codec")
                includeModule("org.apache.commons", "commons-lang3")
                includeModule("org.apache.logging.log4j", "log4j-core")
                includeModule("io.netty", "netty-all")
            }
        }
    }
}

include("core", "api")

rootProject.name = "Schematica Reloaded"
