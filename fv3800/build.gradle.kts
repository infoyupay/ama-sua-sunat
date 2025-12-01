import org.jooq.meta.jaxb.ForcedType

plugins {
    // Standalone exec
    application
    // JavaFX
    id("org.openjfx.javafxplugin") version "0.1.0"
    // jlink packaging
    id("org.beryx.jlink") version "3.1.3"
    // Java support
    java
    // jooq codegen
    id("nu.studer.jooq") version "10.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java {
            srcDir("src/jooq/java")
        }
    }
}


dependencies {
    // --- jooq codegen deps
    jooqGenerator("org.xerial:sqlite-jdbc:3.51.0.0")

    // --- Runtime & DB ---
    implementation("org.xerial:sqlite-jdbc:3.51.0.0")

    // jOOQ core (OSS edition)
    implementation("org.jooq:jooq:3.20.9")

    // ValidatorFX (JavaFX forms)
    implementation("net.synedra:validatorfx:0.6.3")

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:26.0.2-1")

    //DOI Validation
    implementation("com.infoyupay.validator:validator-doi:1.0.0")

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

jooq {
    version.set("3.20.9")

    configurations {
        create("main") {
            jooqConfiguration.apply {

                // JDBC Config
                jdbc.apply {
                    driver = "org.sqlite.JDBC"
                    url = "jdbc:sqlite:$projectDir/model/amasua-3800.db"
                }

                generator.apply {

                    // DB config
                    database.apply {
                        name = "org.jooq.meta.sqlite.SQLiteDatabase"
                        //inputSchema = "main"

                        // ----------- FORCED TYPES -----------
                        forcedTypes = listOf(

                            // BOOLEAN FIELDS
                            ForcedType().apply {
                                name = "BOOLEAN"
                                includeTypes = "INTEGER"
                                includeExpression = ".*\\.(is_.*|has_.*|active|resident)"
                            },

                            // ENUM: ENTITY_SUBTYPE
                            ForcedType().apply {
                                name = "VARCHAR"
                                includeExpression = ".*\\.entity_subtype"
                                userType = "com.yupay.amasua.fv3800.model.enums.EntitySubtype"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.EntitySubtypeConverter"
                            },

                            // ENUM: CODE_LIST
                            ForcedType().apply {
                                name = "VARCHAR"
                                includeExpression = ".*\\.list_id"
                                userType = "com.yupay.amasua.fv3800.model.enums.ListIds"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.ListIdsConverter"
                            },

                            // ENUM: DOI_TYPE
                            ForcedType().apply {
                                name = "VARCHAR"
                                includeExpression = ".*\\.[a-z_]*doi_type$"
                                userType = "com.yupay.amasua.fv3800.model.enums.DoiType"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.DoiTypeConverter"
                            },

                            // ENUM: MARITAL_STATUS
                            ForcedType().apply {
                                name = "VARCHAR"
                                includeExpression = ".*\\.marital_status"
                                userType = "com.yupay.amasua.fv3800.model.enums.MaritalStatus"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.MaritalStatusConverter"
                            },

                            // ENUM: INDIRECT_TYPE
                            ForcedType().apply {
                                name = "VARCHAR"
                                includeExpression = ".*\\.indirect_type"
                                userType = "com.yupay.amasua.fv3800.model.enums.IndirectType"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.IndirectTypeConverter"
                            },

                            // DATE: of epoch day
                            ForcedType().apply {
                                name = "BIGINT"
                                includeExpression = ".*\\.(birthday|.*_date|.*_at)$"
                                userType = "java.time.LocalDate"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.EpochDaysConverter"
                            },

                            // UNSCALED DECIMALS: integers, 6 decimals
                            ForcedType().apply {
                                name = "BIGINT"
                                includeExpression = ".*\\.(value_.*|.*_percentage)$"
                                excludeExpression = ".*\\.value_type"
                                userType = "java.math.BigDecimal"
                                converter = "com.yupay.amasua.fv3800.model.jooq.converters.UnscaledDecimal6"
                            }
                        )
                    }

                    // Output target
                    target.apply {
                        packageName = "com.yupay.amasua.fv3800.model.jooq"
                        directory = "$projectDir/src/jooq/java"
                    }

                    // POJO generation
                    generate.apply {
                        isPojos = true
                        isDaos = false
                    }
                }
            }
        }
    }
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
