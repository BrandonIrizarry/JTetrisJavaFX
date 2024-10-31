import org.gradle.internal.os.OperatingSystem;

group = "xyz.brandonirizarry"

plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.0.1"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://brandonirizarry.xyz/maven/")
    }
}

application {
    mainClass = "xyz.brandonirizarry.jtetrisjavafx.app.Main"
    mainModule = "xyz.brandonirizarry.JTetrisJavaFX"
}

dependencies {
    implementation("xyz.brandonirizarry:jtetris-backend:1.0-SNAPSHOT")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml", "javafx.media")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

jlink {
    options = listOf("--strip-debug", "--compress", "zip-9", "--no-header-files", "--no-man-pages")

    launcher {
        name = "JTetrisFX"
    }

    jpackage {
        if(OperatingSystem.current().isMacOsX) {
            jvmArgs = listOf("-Duser.dir=/tmp")
        } else if(OperatingSystem.current().isWindows) {
            installerOptions = listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu")
        }

        installerOptions.plusAssign("--verbose")
    }
}
