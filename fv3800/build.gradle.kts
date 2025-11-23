plugins {
    // Standalone exec
    application
    // JavaFX
    id("org.openjfx.javafxplugin") version "0.1.0"
    // jlink packaging
    id("org.beryx.jlink") version "3.1.3"
    // Java support
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Runtime & DB ---
    implementation("org.xerial:sqlite-jdbc:3.51.0.0")

    // jOOQ core (OSS edition)
    implementation("org.jooq:jooq:3.20.9")

    // ValidatorFX (JavaFX forms)
    implementation("net.synedra:validatorfx:0.6.3")

    // --- Testing ---
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.assertj:assertj-core:3.27.6")
}

tasks.test {
    useJUnitPlatform()
}

application {
    // NOTE: change to main class later.
    mainClass.set("org.infoyupay.amasua3800.Main")
}

javafx {
    version = "25.0.1"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.base"
    )
}

jlink {
    // Runtime image name
    imageName.set("amasua3800-runtime")

    // Reduce runtime image
    options.set(
        listOf(
            "--strip-debug",
            "--compress", "2",
            "--no-header-files",
            "--no-man-pages"
        )
    )

    launcher {
        name = "amasua3800"
    }
}

// --- OPTIONAL / FUTURE ---
// When Docker aesshell is integrated,
// here we will add the library (JNI or CLI wrapper).
configurations {
    create("integration")
}

// placeholder to avoid warnings
dependencies {
    // example:
    // integration("com.whatever:aesshell-binding:1.0.0")
}
