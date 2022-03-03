package offlinedependencies.maven

import org.apache.maven.model.Dependency
import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.ModelSource2
import org.apache.maven.model.resolution.ModelResolver
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.artifacts.result.UnresolvedArtifactResult
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact
import org.apache.maven.model.building.FileModelSource
import java.io.File

class PomDependencyModelResolver(private val project: Project) : ModelResolver  {
    private val pomCache: MutableMap<String, FileModelSource> = mutableMapOf()
    val componentCache: MutableMap<ModuleComponentIdentifier, File> = mutableMapOf()
    override fun resolveModel(
        groupId: String?,
        artifactId: String?,
        version: String?
    ): ModelSource2? {
        return if (artifactId != null && version != null) {
            val id = "$groupId:$artifactId:$version"
            if (!pomCache.containsKey(id)) {
                resolveArtifactToCache(groupId, artifactId, version, id)
            } else {
                pomCache[id]
            }
        } else {
            null
        }
    }

    private fun resolveArtifactToCache(
        groupId: String?,
        artifactId: String,
        version: String,
        id: String
    ): ModelSource2? {
        val mavenArtifacts = project.dependencies.createArtifactResolutionQuery()
            .forComponents(
                DefaultModuleComponentIdentifier.newId(
                    DefaultModuleIdentifier.newId(
                        groupId,
                        artifactId
                    ), version
                )
            )
            .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
            .execute()
        val component = mavenArtifacts.resolvedComponents.first()
        val poms = component.getArtifacts(MavenPomArtifact::class.java)
        return if (poms.isEmpty()) {
            null
        } else {
            when (val pomArtifact = poms.first()) {
                is UnresolvedArtifactResult -> {
                    project.logger.error(
                        "Resolver was unable to resolve artifact '{}'",
                        pomArtifact.id,
                        pomArtifact.failure
                    )
                    null
                }
                is ResolvedArtifactResult -> {
                    val pomFile = pomArtifact.file
                    val componentId = DefaultModuleComponentIdentifier.newId(
                        DefaultModuleIdentifier.newId(
                            groupId,
                            artifactId
                        ),
                        version
                    )
                    componentCache[componentId] = pomFile
                    val pom = FileModelSource(pomFile)

                    pomCache[id] = pom
                    pom
                }
                else -> {
                    null
                }
            }
        }
    }

    override fun resolveModel(parent: Parent?): ModelSource2? =
        resolveModel(parent?.groupId, parent?.artifactId, parent?.version)

    override fun resolveModel(dependency: Dependency?): ModelSource2? =
        resolveModel(dependency?.groupId, dependency?.artifactId, dependency?.version)

    override fun addRepository(repository: Repository?) {
//        repository?.let { repo ->
//            project.logger.info("[PomDepModelResolver] asked to add repo $repo")
//        }
    }

    override fun addRepository(repository: Repository?, replace: Boolean) {
//        repository?.let { repo ->
//            project.logger.info("[PomDepModelResolver] asked, with replace=$replace to add repo $repo")
//        }
    }

    override fun newCopy(): ModelResolver {
        return this
    }
}