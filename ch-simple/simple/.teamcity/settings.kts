import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.
*/

version = "2019.2"

project {
    // Project description
    description = "Simple Chapter Project Example"

    // VCS Root definition
    val vcsRoot = GitVcsRoot {
        id("SimpleProjectVcs")
        name = "Simple Project Git Repository"
        url = "https://github.com/username/simple-project.git" // Replace with actual repository URL
        branch = "refs/heads/main"
        branchSpec = "+:refs/heads/*"
    }
    vcsRoot(vcsRoot)

    // Store build type references
    val buildConf = BuildType {
        id("SimpleProjectBuild")
        name = "Build"
        description = "Builds the Simple Project"

        vcs {
            root(vcsRoot)
        }

        // Build steps
        steps {
            // Clean before build
            maven {
                name = "Clean"
                goals = "clean"
                runnerArgs = "-Dmaven.test.skip=true"
            }

            // Compile and package
            maven {
                name = "Compile and Package"
                goals = "package"
                runnerArgs = "-Dmaven.test.skip=true"
            }
        }

        // Triggers
        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }

        // Features
        features {
            perfmon {
            }
        }

        // Artifact rules
        artifactRules = "target/*.jar"
    }

    val testConf = BuildType {
        id("SimpleProjectTest")
        name = "Test"
        description = "Runs tests for the Simple Project"

        vcs {
            root(vcsRoot)
        }

        // Build steps
        steps {
            // Run tests
            maven {
                name = "Run Tests"
                goals = "test"
            }
        }

        // Dependencies
        dependencies {
            snapshot(buildConf) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }

        // Features
        features {
            perfmon {
            }
        }
    }

    val deployConf = BuildType {
        id("SimpleProjectDeploy")
        name = "Deploy"
        description = "Deploys the Simple Project"

        vcs {
            root(vcsRoot)
        }

        // Build steps
        steps {
            // Deploy step (example using script)
            script {
                name = "Deploy to Environment"
                scriptContent = """
                    echo "Deploying Simple Project..."
                    # Add deployment commands here
                    echo "Deployment completed successfully!"
                """.trimIndent()
            }
        }

        // Dependencies
        dependencies {
            snapshot(testConf) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }

        // Features
        features {
            perfmon {
            }
        }
    }

    // Register build types
    buildType(buildConf)
    buildType(testConf)
    buildType(deployConf)

    // Build chain
    sequential {
        buildType(buildConf)
        buildType(testConf)
        buildType(deployConf)
    }
}
