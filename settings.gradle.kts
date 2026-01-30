pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()

    // [KUP] set kotlin_repo_url for plugins
    providers.gradleProperty("kotlin_repo_url").orNull?.let { kotlinRepoUrl ->
      logger.info("<KUP> Repo plugins URL: $kotlinRepoUrl")
      maven(kotlinRepoUrl) { name = "KotlinDev" }
    }
  }
}

plugins {
  id("com.gradle.develocity") version "4.3"
  id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.1.6"
}

develocity {
  buildScan {
    publishing.onlyIf { false }
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

gitHooks {
  commitMsg { conventionalCommits() }
  preCommit { tasks("ktfmtFormat") }
  hook("pre-push") { tasks("ktfmtCheck") }
  createHooks(overwriteExisting = true)
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    gradlePluginPortal()

    // [KUP] set kotlin_repo_url for dependencies
    providers.gradleProperty("kotlin_repo_url").orNull?.let { kotlinRepoUrl ->
      logger.info("<KUP> Repo dependencies URL: $kotlinRepoUrl")
      maven(kotlinRepoUrl) { name = "KotlinDev" }
    }
  }

  versionCatalogs {
    create("libs") {
      // [KUP] set kotlin_version
      providers.gradleProperty("kotlin_version").orNull?.let { customKotlinVersion ->
        logger.info("<KUP> Kotlin version: $customKotlinVersion")
        version("kotlin", customKotlinVersion)
      }
    }
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "npm-publish"

include(":npm-publish-gradle-plugin", ":npm-publish-docs")
