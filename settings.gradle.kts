pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  includeBuild("./build-conventions")
  includeBuild("./build-konventions")
}

plugins {
  id("settings")
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "npm-publish"
include(
  "npm-publish-gradle-plugin",
  "npm-publish-docs",
)
