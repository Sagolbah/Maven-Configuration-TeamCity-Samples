import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

/*
* TeamCity Kotlin DSL for Maven Configuration TeamCity Samples
* This DSL defines two equal build configurations with a snapshot dependency
*/

version = "2023.05"

project {
    // Project description
    description = "Maven Configuration TeamCity Samples"
    
    // Define VCS Root
    val vcsRoot = GitVcsRoot {
        id("MavenConfigurationTeamCitySamples_VcsRoot")
        name = "Maven Configuration TeamCity Samples VCS Root"
        url = "https://github.com/JetBrains/Maven-Configuration-TeamCity-Samples.git"
        branch = "refs/heads/master"
    }
    
    vcsRoot(vcsRoot)
    
    // First build configuration
    val buildConfig1 = BuildType {
        id("MavenConfigurationTeamCitySamples_Build1")
        name = "Build and Test (1)"
        
        vcs {
            root(vcsRoot)
        }
        
        steps {
            maven {
                name = "Clean and Test"
                goals = "clean test"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                userSettingsSelection = "settings.xml"
                localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
                jdkHome = "%env.JDK_11%"
            }
        }
        
        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }
        
        features {
            perfmon {
            }
        }
    }
    
    // Second build configuration (equal to the first one)
    val buildConfig2 = BuildType {
        id("MavenConfigurationTeamCitySamples_Build2")
        name = "Build and Test (2)"
        
        vcs {
            root(vcsRoot)
        }
        
        steps {
            maven {
                name = "Clean and Test"
                goals = "clean test"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                userSettingsSelection = "settings.xml"
                localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
                jdkHome = "%env.JDK_11%"
            }
        }
        
        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }
        
        features {
            perfmon {
            }
        }
        
        // Add snapshot dependency on the first build configuration
        dependencies {
            snapshot(buildConfig1) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }
    
    // Register both build configurations
    buildType(buildConfig1)
    buildType(buildConfig2)
}