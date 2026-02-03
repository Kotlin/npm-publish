@file:Suppress("UnstableApiUsage")

import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.plugin.serialization)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.signing)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.plugin.publish)
}

description =
  """
  A maven-publish alternative for NPM package publishing.
  Integrates with kotlin JS/KMP plugins (if applied) to automatically
  setup publishing to NPM repositories for all JS targets.
  """
    .trimIndent()

java { targetCompatibility = JavaVersion.VERSION_11 }

val defaultLanguageVersion = KotlinVersion.KOTLIN_2_1

// [KUP] set kotlin_language_version
val providedLanguageVersion =
  providers.gradleProperty("kotlin_language_version").map(KotlinVersion::fromVersion).orNull?.also {
    logger.info("<KUP> Kotlin language version: $it")
  } ?: defaultLanguageVersion

// [KUP] set kotlin_api_version
val providedApiVersion =
  providers.gradleProperty("kotlin_api_version").map(KotlinVersion::fromVersion).orNull?.also {
    logger.info("<KUP> Kotlin API version: $it")
  } ?: providedLanguageVersion

// [KUP] set kotlin_additional_cli_options
val kotlinAdditionalCliOptions = run {
  val spacesRegex = "\\s+".toRegex()
  providers
    .gradleProperty("kotlin_additional_cli_options")
    .map { option ->
      option.trim { it == '"' || it.isWhitespace() }.split(spacesRegex).filterNot(String::isEmpty)
    }
    .orNull
    ?.also { logger.info("<KUP> Kotlin additional CLI options: $it") }
    .orEmpty()
}

kotlin {
  jvmToolchain(21)
  explicitApi()

  compilerOptions {
    languageVersion.set(providedLanguageVersion)
    apiVersion.set(providedApiVersion)
    freeCompilerArgs.addAll("-Xreport-all-warnings", "-Xrender-internal-diagnostic-names")
    freeCompilerArgs.addAll(kotlinAdditionalCliOptions)
  }
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
  vcsUrl = "https://github.com/Kotlin/npm-publish"
  plugins {
    create(name) {
      id = "org.jetbrains.kotlin.npm-publish"
      implementationClass = "dev.petuska.npm.publish.NpmPublishPlugin"
      displayName = "NPM package publishing to NPM repositories"
      description = project.description
      tags = listOf("npm", "publishing", "kotlin", "node", "js")

      compatibility {
        features {
          configurationCache = true
        }
      }
    }
  }
}

signWith(
  keyId = secret("libs.sign.key.id"),
  signingKey = secret("libs.sign.key.private"),
  signingPassphrase = secret("libs.sign.passphrase"),
)

publishing {
  repositories {
    maven(
      url = secret("libs.repo.url")?.let(::uri),
      username = secret("libs.repo.user"),
      password = secret("libs.repo.password"),
    )
  }

  projectInfo {
    name = "npm-publish"
    url = "https://github.com/Kotlin/${rootProject.name.lowercase()}"
    description = provider { project.description }

    licenses {
      license {
        name = "Apache-2.0"
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        distribution = "repo"
      }
    }

    developers {
      developer {
        name = "Martynas Petuška"
        email = "martynas@petuska.dev"
      }
      developer {
        id = "JetBrains"
        name = "JetBrains Team"
        organization = "JetBrains"
        organizationUrl = "https://www.jetbrains.com"
      }
    }

    scm { url = "https://github.com/Kotlin/${rootProject.name.lowercase()}" }
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
