plugins { id("com.gradle.develocity") version "4.3" }

develocity {
  buildScan {
    publishing.onlyIf { false }
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "sandbox"

includeBuild("../")

include(":both", ":browser", ":empty", ":node")
