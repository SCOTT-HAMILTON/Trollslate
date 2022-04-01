package offlinedependencies

import offlinedependencies.OfflineDependenciesPlugin.Companion.DEFAULT_OFFLINE_REPO_DIR
import offlinedependencies.OfflineDependenciesPlugin.Companion.offlineRepositoryRootProp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.support.serviceOf

class OfflineDependenciesPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "offlineDependencies"
        const val TASK_NAME = "updateOfflineRepositoryTask"
        const val DEFAULT_OFFLINE_REPO_DIR = "offline-repository"
        const val offlineRepositoryRootProp = "offlineRepositoryRoot"
    }
    override fun apply(target: Project) {
        val offlineRepoRootDir = getOfflineRepoDir(target)
        val repoHandler: RepositoryHandler =
            DefaultRepositoryHandler(target.serviceOf(), target.serviceOf(), target.serviceOf())
        target.extensions.create(
            EXTENSION_NAME,
            OfflineDependenciesExtension::class.java,
            repoHandler
        )
        target.logger.info("Offline dependencies root configured at $offlineRepoRootDir")
        target.tasks.create(TASK_NAME, UpdateOfflineRepositoryTask::class.java)
    }
}

fun getOfflineRepoDir(target: Project): String {
    val defaultDir = "${target.projectDir}/$DEFAULT_OFFLINE_REPO_DIR"
    return if (!target.hasProperty(offlineRepositoryRootProp)) {
        target.extra.properties[offlineRepositoryRootProp] = defaultDir
        defaultDir
    } else {
        target.properties[offlineRepositoryRootProp]?.toString() ?: defaultDir
    }
}
