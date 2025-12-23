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
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "npm-publish"

include(":npm-publish-gradle-plugin", ":npm-publish-docs")
