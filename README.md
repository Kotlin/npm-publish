[![Slack chat](https://img.shields.io/badge/kotlinlang-%23npm--publish-darkgreen?logo=slack&style=flat-square)](https://kotlinlang.slack.com/channels/npm-publish)
[![Mkdocs docs](https://img.shields.io/badge/docs-mkdocs-blue?style=flat-square&logo=kotlin&logoColor=white)](https://npm-publish.petuska.dev)
[![Version gradle-plugin-portal](https://img.shields.io/maven-metadata/v?label=gradle%20plugin%20portal&logo=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Forg.jetbrains.kotlin%2Fnpm-publish-gradle-plugin%2Fmaven-metadata.xml&style=flat-square)](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.npm-publish)
[![Version maven-central](https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Forg%2Fjetbrains%2Fkotlin%2Fnpm-publish-gradle-plugin%2Fmaven-metadata.xml&label=maven-central&logo=apache-maven&style=flat-square)](https://mvnrepository.com/artifact/org.jetbrains.kotlin/npm-publish-gradle-plugin/latest)

# NPM-PUBLISH GRADLE PLUGIN

Gradle plugin enabling NPM publishing (essentially `maven-publish` for NPM packages). Integrates seamlessly with
Kotlin/KMP plugin if applied, providing auto configurations.

> Check the latest [release](https://github.com/Kotlin/npm-publish/releases/latest) for verified JVM, Kotlin and
> Gradle tooling versions

## Setup

Here's a bare minimum setup when using together with one of the kotlin plugins. This setup would produce the following
tasks:

* `assembleJsPackage`
* `packJsPackage`
* `publishJsPackageToNpmjsRegistry`

```kotlin title="build.gradle.kts"
plugins {
    kotlin("npm-publish") version "<VERSION>"
    kotlin("multiplatform") version "<VERSION>>" // Optional, also supports "js"
}

kotlin {
    js(IR) {
        binaries.library()
        browser() // or nodejs()
    }
}

npmPublish {
    registries {
        // For registries expecting an authentiation token, use authToken
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            authToken.set("obfuscated")
        }
        
        // For registries expecting a username and password, use auth or username + password
        register("nexus") {
            uri.set("https://nexus.example.com/repository/npm-internal")
            username.set("obfuscated")
            password.set("obfuscated")
            // Or:
            // auth.set("base64-encoded-string")
        }
    }
}
```

Full documentation can be found
on [npm-publish.petuska.dev](https://npm-publish.petuska.dev/latest/user-guide/quick-start/)

> Support for the org.jetbrains.kotlin.js plugin has been removed in version 3.7.0. Use prior versions if you are using
> the legacy Kotlin/JS plugin or migrate to the Kotlin/Multiplatform plugin.

## Important note

The project was developed by Martynas Petuška ([mpetuska](https://github.com/mpetuska)) for a long time, and after Martynas no longer had the capacity to maintain this plugin
We ([JetBrains](https://github.com/JetBrains)), by mutual agreement with Martynas, took over the repository to further develop the plugin, 
ensuring our awesome community would not be left without the essential feature of publishing KMP libraries to NPM.

We would like to extend a huge thank-you to Martynas and all the contributors for developing such a great plugin!

The plugin originally was under [Unlincense](https://unlicense.org/) license, but after the migration to [Kotlin org](https://github.com/Kotlin),
there was a decision to migrate to the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) license.

The last version of code under [Unlincense](https://unlicense.org/) license is placed in this repo in the [licensed-with-unlicense](https://github.com/Kotlin/npm-publish/tree/licensed-with-unlicense) branch.
The last version of the plugin published under [Unlincense](https://unlicense.org/) is [v3.5.3](https://github.com/Kotlin/npm-publish/releases/tag/3.5.3).

All the versions after [v3.5.3](https://github.com/Kotlin/npm-publish/releases/tag/3.5.3) are under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## Contributing

See [CONTRIBUTING](.github/CONTRIBUTING.md)

Thanks to all the people who contributed to npm-publish!

<a href="https://github.com/Kotlin/npm-publish/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Kotlin/npm-publish" />
</a>
