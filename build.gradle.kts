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

tasks.jar {
    exclude("bundle")
}

val packedJar by tasks.registering(Jar::class) {
    group = "build"
    from(sourceSets.main.get().output)
    exclude("dev/rdh/idk/packed/**")

    archiveClassifier.set("packed")
}

configurations.all {
    resolutionStrategy.force("org.ow2.asm:asm:$asmVersion")
    resolutionStrategy.force("org.ow2.asm:asm-tree:$asmVersion")
}

tasks.register<JavaExec>("runPacker") {
    mainClass = "dev.rdh.pcl.Packer"
    classpath = sourceSets.main.get().runtimeClasspath

    val base = file("build/classes/java/main")

    args("src/main/resources/bundle",
        base.absolutePath,
        *file("build/classes/java/main/dev/rdh/pcl/packed/").listFiles()!!.map { it.absolutePath }.toTypedArray()
    )
}

tasks.register<JavaExec>("runClassLoaderTest") {
    mainClass = "dev.rdh.pcl.PackedClassLoader"
    classpath = packedJar.get().outputs.files
}