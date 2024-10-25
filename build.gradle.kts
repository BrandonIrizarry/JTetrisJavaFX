group = "xyz.brandonirizarry"

plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://brandonirizarry.xyz/maven/")
    }
}

application {
    mainClass = "xyz.brandonirizarry.jtetrisjavafx.Main"
    mainModule = "xyz.brandonirizarry.JTetrisJavaFX"
}

dependencies {
    implementation("xyz.brandonirizarry:jtetris-backend:1.0")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml", "javafx.media")
}
