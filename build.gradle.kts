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

tasks.register<JavaExec>("runPacker") {
    mainClass = "dev.rdh.idk.Packer"
    classpath = sourceSets.main.get().runtimeClasspath
    args("src/main/resources/bundle.pack",
        "build/classes/java/main",
        "build/classes/java/main/dev/rdh/idk/TestMain.class",
        "build/classes/java/main/dev/rdh/idk/TestMain\$InnerClass.class")
}

tasks.register<JavaExec>("runClassLoaderTest") {
    mainClass = "dev.rdh.idk.CustomClassLoader"
    classpath = tasks.jar.get().outputs.files
}