dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "samples"

includeBuild("../")

include(":publish-to-github-packages", ":no-kotlin-plugin", ":local-ts-consumer:kt")
