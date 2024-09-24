plugins {
    id("java")
}

group = "dev.rdh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    sourceCompatibility = JavaVersion.VERSION_1_8
}

val asmVersion = "9.7"
dependencies {
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-tree:$asmVersion")

    annotationProcessor("net.auoeke:uncheck:0.3.8")
}

tasks.compileJava {
    options.compilerArgs.add("-Xplugin:uncheck")
    options.release = 8
}

configurations.all {
    resolutionStrategy.force("org.ow2.asm:asm:$asmVersion")
    resolutionStrategy.force("org.ow2.asm:asm-tree:$asmVersion")
}

tasks.register<JavaExec>("runUnpacker") {
    mainClass = "dev.rdh.pack201.Unpacker"
    classpath = sourceSets.main.get().runtimeClasspath
    args("output.pack", "output/**")
}