import org.gradle.kotlin.dsl.assign

/*
 * Copyright 2017-2025 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENCE file.
 */

plugins {
  `maven-publish`
  signing
}

val deployVersion: String? = project.findProperty("DeployVersion")?.toString()?.ifBlank { null }
if (deployVersion != null) project.version = deployVersion

signing {
  val keyId = project.getSensitiveProperty("libs.sign.key.id")
  val signingKey = project.getSensitiveProperty("libs.sign.key.private")
  val signingKeyPassphrase = project.getSensitiveProperty("libs.sign.passphrase")
  if (!signingKey.isNullOrBlank()) {
      useInMemoryPgpKeys(keyId, signingKey, signingKeyPassphrase)
      sign(publishing.publications)
  } else {
    isRequired = false
  }
}

publishing {
  repositories {
    configureMavenPublication(project)
  }

  publications.withType<MavenPublication>().configureEach {
    pom {
      url = "https://github.com/Kotlin/npm-publish"
      description = provider { project.description }

      licenses {
        // TODO: update
        license {
          name = "Unlicense"
          url = "https://unlicense.org"
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
      scm {
        url = "https://github.com/Kotlin/npm-publish"
      }
    }
  }
}

fun RepositoryHandler.configureMavenPublication(project: Project) {
  val repositoryUrl = project.getSensitiveProperty("libs.repo.url")
  if (!repositoryUrl.isNullOrBlank()) {
    maven {
      url = uri(repositoryUrl)
      credentials {
        username = project.getSensitiveProperty("libs.repo.user")
        password = project.getSensitiveProperty("libs.repo.password")
      }
    }
  }
}

fun Project.getSensitiveProperty(name: String): String? {
  return project.findProperty(name) as? String ?: System.getenv(name)
}
