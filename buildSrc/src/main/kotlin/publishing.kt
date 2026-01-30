import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

fun Project.secret(key: String): String? =
  findProperty(key) as? String ?: System.getenv(key)

fun PublishingExtension.projectInfo(configuration: MavenPom.() -> Unit) {
  publications.withType<MavenPublication>().configureEach {
    pom(configuration)
  }
}

fun Project.signWith(
  keyId: String?,
  signingKey: String?,
  signingPassphrase: String?,
) {
  val signing = extensions.getByType(SigningExtension::class.java)
    // For KUP only
    .apply { isRequired = keyId != null }

  signing.useInMemoryPgpKeys(
    keyId,
     signingKey,
    signingPassphrase
  )

  val publishing = extensions.getByType(PublishingExtension::class.java)
  signing.sign(publishing.publications)
}

fun RepositoryHandler.maven(
  url: URI?,
  username: String?,
  password: String?
) {
  val repositoryUrl = url ?: return
  maven {
    this.url = repositoryUrl
    credentials {
      this.username = username
      this.password = password
    }
  }
}