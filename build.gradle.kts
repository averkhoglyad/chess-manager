import com.excelsiorjet.api.tasks.config.excelsiorinstaller.ExcelsiorInstallerConfig
import com.excelsiorjet.gradle.plugin.ExcelsiorJetExtension
import groovy.lang.Closure
import org.gradle.api.internal.plugins.PluginApplicationException
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "averkhoglyad"
version = "0.1.2-ALFA"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.41"

    repositories {
        mavenCentral()
    }

    val jetPluginVersion = "1.1.3"

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
        classpath("com.excelsiorjet:excelsior-jet-gradle-plugin:$jetPluginVersion")
    }
}

apply {
    plugin("java")
    plugin("kotlin")
    plugin("application")
    plugin("excelsiorJet")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    compile("org.picocontainer:picocontainer:2.15")
    compile("no.tornado:tornadofx:1.7.15")
    compile("org.controlsfx:controlsfx:8.40.14")
    compile("org.apache.httpcomponents:httpclient:4.5.5")
    compile("com.fasterxml.jackson.core:jackson-core:2.9.5")
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.5")
    compile("com.fasterxml.jackson.core:jackson-annotations:2.9.5")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.5")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("org.slf4j:jul-to-slf4j:1.7.25")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

var mainClassName = "net.averkhoglyad.chess.manager.MainKt"
project.setProperty("mainClassName", mainClassName)

configure<ExcelsiorJetExtension> {
    mainClass = mainClassName
    hideConsole = true
    splash = File("src/main/jetresources/splash.jpg")
    outputName = "chess-manager"
//    globalOptimizer = true
//    packaging = "excelsior-installer"
//    excelsiorInstaller = ExcelsiorInstallerConfig().apply {
//        vendor = "AWer"
//    }
}

