/*
 * TeamCity DSL Settings File
 * 
 * This file defines the build configurations for the Maven Configuration TeamCity Samples project.
 * 
 * NOTE: This file will show errors in the editor due to missing TeamCity DSL libraries,
 * but it will be processed correctly when imported into TeamCity.
 * 
 * The file defines:
 * 1. A Maven build configuration that builds the project
 * 2. A second, equal build configuration that depends on the first one
 * 3. Artifact publishing for both configurations
 */

// Import statements for TeamCity DSL
import jetbrains.buildServer.configs.kotlin.v2023_05.*
import jetbrains.buildServer.configs.kotlin.v2023_05.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2023_05.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2023_05.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2023_05.dependencies.snapshot

// Version of the TeamCity DSL
version = "2023.05"

// Project definition
project {
    // Project description
    description = "Maven Configuration TeamCity Samples"

    // First build configuration
    val mavenBuild = BuildType {
        id("MavenBuild")
        name = "Maven Build"
        description = "Builds the project using Maven"

        // VCS settings
        vcs {
            root(DslContext.settingsRoot)
        }

        // Build steps
        steps {
            maven {
                goals = "clean test"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                userSettingsSelection = "default"
                jdkHome = "%env.JDK_11_0%"
            }
        }

        // Triggers
        triggers {
            vcs {}
        }

        // Features
        features {
            perfmon {}
        }

        // Artifacts
        artifactRules = """
            ch-simple/simple/target/*.jar => artifacts
            ch-simple/simple/target/surefire-reports => reports
        """.trimIndent()
    }

    // Second build configuration that depends on the first one
    val mavenBuildDependent = BuildType {
        id("MavenBuildDependent")
        name = "Maven Build Dependent"
        description = "Second build configuration that depends on the first one"

        // VCS settings
        vcs {
            root(DslContext.settingsRoot)
        }

        // Build steps
        steps {
            maven {
                goals = "clean test"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                userSettingsSelection = "default"
                jdkHome = "%env.JDK_11_0%"
            }
        }

        // Triggers
        triggers {
            vcs {}
        }

        // Features
        features {
            perfmon {}
        }

        // Artifacts
        artifactRules = """
            ch-simple/simple/target/*.jar => artifacts
            ch-simple/simple/target/surefire-reports => reports
        """.trimIndent()

        // Dependencies
        dependencies {
            snapshot(mavenBuild) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }

    // Register build configurations
    buildType(mavenBuild)
    buildType(mavenBuildDependent)
}