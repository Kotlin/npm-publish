@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.plugin.serialization)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.deployer)
  //  id("full-publishing")
  alias(libs.plugins.plugin.publish)
}

description =
  """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/MPP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
  """
    .trimIndent()

java { targetCompatibility = JavaVersion.VERSION_11 }

kotlin {
  jvmToolchain(21)
  explicitApi()
}

dependencies {
  compileOnly(libs.plugin.kotlin)
  compileOnly(libs.plugin.node.gradle)

  testImplementation(kotlin("test"))
  testImplementation(libs.plugin.kotlin)
  testImplementation(libs.bundles.kotest.assertions)
}

gradlePlugin {
  website = "https://npm-publish.petuska.dev"
  vcsUrl = "https://github.com/mpetuska/npm-publish"
  plugins {
    create(name) {
      id = "dev.petuska.npm.publish"
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
      displayName = "NPM package publishing to NPM repositories"
      description = project.description
      tags = listOf("npm", "publishing", "kotlin", "node", "js")
    }
  }
}

deployer {
  content { gradlePluginComponents() }
  projectInfo {
    url = "https://github.com/Kotlin/${rootProject.name.lowercase()}"
    description = provider { project.description }
    license {
      name = "Unlicense"
      url = "https://unlicense.org"
    }
    developer {
      name = "Martynas Petuška"
      email = "martynas@petuska.dev"
    }
    scm { fromGithub("Kotlin", rootProject.name.lowercase()) }
  }
  signing {
    key = secret("SIGNING_PGP_KEY")
    password = secret("SIGNING_PGP_PASSWORD")
  }
  centralPortalSpec {
    allowMavenCentralSync = false
    auth {
      user = secret("REPOSITORY_CENTRAL_USERNAME")
      password = secret("REPOSITORY_CENTRAL_PASSWORD")
    }
  }
  githubSpec {
    owner = "Kotlin"
    repository = rootProject.name.lowercase()
    auth {
      user = secret("REPOSITORY_GITHUB_USERNAME")
      token = secret("REPOSITORY_GITHUB_PASSWORD")
    }
  }
}

ktfmt { googleStyle() }

tasks {
  withType<Test> { useJUnitPlatform() }
  withType<KotlinJvmCompile> { compilerOptions.jvmTarget = JvmTarget.JVM_11 }
  register<Jar>("javadocJar") {
    from(dokkatooGeneratePublicationHtml)
    archiveClassifier = "javadoc"
  }
  whenTaskAdded {
    if (
      name.contains("CentralPortal", ignoreCase = true) ||
        name.contains("Github", ignoreCase = true)
    ) {
      dependsOn("javadocJar", "sourcesJar", "jar", "makeEmptyDocsJar", "makeEmptySourcesJar")
    }
  }
}
