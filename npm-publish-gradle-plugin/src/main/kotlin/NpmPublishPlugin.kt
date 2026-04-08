/*
 * Copyright 2025-2026 JetBrains s.r.o. and respective authors and developers.
 * Based on original work by Martynas Petuska and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package dev.petuska.npm.publish

import dev.petuska.npm.publish.config.configure
import dev.petuska.npm.publish.config.configureNodeGradlePlugin
import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.task.NodeExecTask
import dev.petuska.npm.publish.task.NpmAssembleTask
import dev.petuska.npm.publish.task.NpmPackTask
import dev.petuska.npm.publish.task.NpmPublishTask
import dev.petuska.npm.publish.util.configure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.utils.named

/** Main entry point for npm-publish plugin */
@Suppress("unused")
public class NpmPublishPlugin : Plugin<Project> {
  override fun apply(project: Project): Unit =
    with(project) {
      val extension = extensions.create(NpmPublishExtension.NAME, NpmPublishExtension::class.java)
      configure(extension)
      configureNodeGradlePlugin(extension)
      pluginManager.withPlugin(KOTLIN_MPP_PLUGIN) {
        extensions.configure<KotlinMultiplatformExtension> {
          targets.filterIsInstance<KotlinJsTargetDsl>().forEach { configure(it) }
          targets.whenObjectAdded { if (it is KotlinJsTargetDsl) configure(it) }
          targets.whenObjectRemoved {
            if (it is KotlinJsTargetDsl)
              extension.packages.findByName(it.name)?.let(extension.packages::remove)
          }
        }
      }

      afterEvaluate {
        if (tasks.names.contains("kotlinNodeJsSetup")) {
          tasks
            .named<NodeJsSetupTask>("kotlinNodeJsSetup")
            .flatMap { layout.dir(it.destinationProvider.asFile) }
            .let(extension.nodeHome::convention)
          // Hack to work around all KGP kotlinNodeJsSetup tasks sharing the same output dir.
          rootProject.allprojects { subProject ->
            tasks.withType(NodeExecTask::class.java) {
              it.mustRunAfter(subProject.tasks.withType(NodeJsSetupTask::class.java))
            }
          }
        }
        tasks.maybeCreate("assemble").apply {
          group = "build"
          dependsOn(tasks.withType(NpmAssembleTask::class.java))
        }
        tasks.maybeCreate("pack").apply {
          group = "build"
          dependsOn(tasks.withType(NpmPackTask::class.java))
        }
        tasks.maybeCreate(PUBLISH_LIFECYCLE_TASK_NAME).apply {
          group = PUBLISH_TASK_GROUP
          dependsOn(tasks.withType(NpmPublishTask::class.java))
        }
      }
    }

  private companion object {
    private const val KOTLIN_MPP_PLUGIN = "org.jetbrains.kotlin.multiplatform"
  }
}
