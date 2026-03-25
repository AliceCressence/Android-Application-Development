plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.gradecalculator"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

application {
    mainClass.set("MainKt")
    // Allows: ./gradlew run --args="--input input.xlsx --output output.xlsx"
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

// Configures the fat JAR produced by the Shadow plugin.
// Running `./gradlew shadowJar` produces:
//   build/libs/student-grade-calculator-1.0.0-all.jar
tasks.shadowJar {
    archiveBaseName.set("student-grade-calculator")
    archiveClassifier.set("all")
    archiveVersion.set("1.0.0")
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}

// Align Java and Kotlin to the same JVM target (21).
// Kotlin 1.9 max supported target is 21, so we pin both here.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}