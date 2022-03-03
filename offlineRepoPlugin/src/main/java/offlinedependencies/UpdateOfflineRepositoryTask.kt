package offlinedependencies

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ArtifactResult
import org.gradle.api.artifacts.result.ComponentArtifactsResult
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.component.Artifact
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import org.gradle.language.java.artifact.JavadocArtifact
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact
import java.io.File
import offlinedependencies.maven.PomDependencyModelResolver
import org.gradle.api.artifacts.result.UnresolvedArtifactResult
import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.building.FileModelSource
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.ModelProblem
import org.apache.maven.model.interpolation.StringVisitorModelInterpolator
import org.apache.maven.model.path.DefaultUrlNormalizer
import org.apache.maven.model.path.DefaultPathTranslator
import org.apache.maven.model.validation.DefaultModelValidator
import org.apache.maven.model.Model
import org.apache.maven.model.building.ModelBuildingException
import org.gradle.api.Plugin
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.ivy.IvyDescriptorArtifact
import org.gradle.ivy.IvyModule
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.plugins
import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class UpdateOfflineRepositoryTask : DefaultTask() {
    private lateinit var offlineRepoDir: String
    data class ArtifactComponent(val id: ModuleComponentIdentifier, val files: MutableSet<File>)
    @TaskAction
    fun run() {
//        project.configurations.first().incoming.resolutionResult.allDependencies
        offlineRepoDir = getOfflineRepoDir(project)
        withRepositoryFiles { components ->
            components.forEach { (id, files) ->
                val directory = moduleDirectory(id)
                directory.mkdirs()
                files.forEach { file ->
                    Files.copy(
                        file.toPath(),
                        File(directory, file.name).toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
            }
        }
    }

    private fun withRepositoryFiles(callback: (Map<ModuleComponentIdentifier, Set<File>>)->Unit) {
        val originalRepos = project.repositories.toList()
//        project.logger.info("Original repos: `$originalRepos`")
//        project.repositories.clear()
//        project.logger.info("Lol repos 1: `${project.repositories.toList()}`")
        val extension =
            project.extensions.getByName(OfflineDependenciesPlugin.EXTENSION_NAME) as OfflineDependenciesExtension
//        project.repositories.addAll(extension.repositoryHandler)
//        project.logger.info("Lol repos 2: `${project.repositories.toList()}`")
//        project.configurations.create("classpath") {
//            dependencies.add(project.buildscript.dependencies.create("com.android.tools.build:gradle:7.2.0-beta02"))
//        }
        val files = collectRepoFiles(getConfigurations())
//        project.repositories.clear()
//        project.logger.info("Lol repos 3: `${project.repositories.toList()}`")
        project.repositories.addAll(originalRepos)
//        project.logger.info("Lol repos 4: `${project.repositories.toList()}`")
        println("Lol files $files")
        callback(files)
    }
    private fun getConfigurations() : Set<Configuration> =
        project.configurations.toSet()
    private fun collectRepoFiles(configurations: Set<Configuration>):
            Map<ModuleComponentIdentifier, Set<File>> {
//        configurations.forEach {
//            project.buildscript.dependencies.add(
//                it.name,
//                "com.android.tools.build:gradle:7.2.0-beta02")
//        }
        logger.info("Config configurations=$configurations")
        val components = configurations.map { config ->
            config.allDependencies.map { dep ->
                getImperativeDepsArtifacts().map {
                    it.id to it.files
                } + dep.artifactToComponents().map {
                    it.id to it.files
                }
            }.flatten()
        }.flatten().toSet().associate { it }.toMutableMap()

        // collect sources and javadocs
        val jvmArtifacts = project.dependencies.createArtifactResolutionQuery()
            .forComponents(components.keys)
            .withArtifacts(JvmLibrary::class.java, SourcesArtifact::class.java, JavadocArtifact::class.java)
            .execute()
        jvmArtifacts.resolvedComponents.forEach { component ->
            // adding sources
            components.addFromComponentArtifacts(component, SourcesArtifact::class.java)
            // adding java docs
            components.addFromComponentArtifacts(component, JavadocArtifact::class.java)
        }

        // collect maven poms
        collectPoms(components)

        // collect ivy xmls
        collectIvyXmls(components)

        return components
    }

    data class SimpleComponentId(val group: String, val name: String, val version: String) {
        fun toModuleCompId() : ModuleComponentIdentifier =
            DefaultModuleComponentIdentifier.newId(
                DefaultModuleIdentifier.newId(group, name),
                version
            )
    }

    fun String.toSimpleComponentId() : SimpleComponentId =
        split(':').let {
            assert(it.size == 3)
            SimpleComponentId(it[0], it[1], it[2])
        }

    private fun collectPoms(components: MutableMap<ModuleComponentIdentifier, MutableSet<File>>) {
        val imperativePomComponents = listOf(
//            SimpleComponentId(
//                "org.gradle.kotlin.kotlin-dsl",
//                "org.gradle.kotlin.kotlin-dsl.gradle.plugin", "2.1.7").toModuleCompId(),
//            SimpleComponentId(
//                "com.google.code.gson",
//                "gson", "2.8.6").toModuleCompId(),
//            SimpleComponentId(
//                "com.google.code.gson",
//                "gson-parent", "2.8.6").toModuleCompId(),
//            SimpleComponentId(
//                "org.sonatype.oss",
//                "oss-parent", "7"
//            ).toModuleCompId(),
//            SimpleComponentId(
//                "com.google.guava",
//                "guava", "29.0-jre"
//            ).toModuleCompId(),
//            SimpleComponentId(
//                "com.google.guava",
//                "guava-parent", "29.0-jre"
//            ).toModuleCompId(),
//            SimpleComponentId(
//                "org.jetbrains.kotlinx",
//                "kotlinx-coroutines-core", "1.5.0"
//            ).toModuleCompId(),
            "com.gradle.enterprise:com.gradle.enterprise.gradle.plugin:3.8.1"
                .toSimpleComponentId().toModuleCompId(),
            "com.android.application:com.android.application.gradle.plugin:7.2.0-beta02"
                .toSimpleComponentId().toModuleCompId(),
            "org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:1.5.31"
                .toSimpleComponentId().toModuleCompId(),
        )
        logger.trace("Collecting pom files")
        components.keys.forEach {
            logger.info("Component POM ID:$it")
        }
        val mavenArtifacts = project.dependencies.createArtifactResolutionQuery()
            .forComponents(
                components.keys + imperativePomComponents
            )
            .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
            .execute()
        val pomModelResolver = PomDependencyModelResolver(project)
        mavenArtifacts.resolvedComponents.forEach { component ->
            val poms = component.getArtifacts(MavenPomArtifact::class.java)
            if (poms.isNotEmpty()) {
                when (val pomArtifact = poms.first()) {
                    is UnresolvedArtifactResult -> {
                        logger.error(
                            "Resolver was unable to resolve artifact '{}'",
                            pomArtifact.id,
                            pomArtifact.failure
                        )
                    }
                    is ResolvedArtifactResult -> {
                        val pomFile = pomArtifact.file
                        resolvePom(pomModelResolver, pomFile)
                        logger.info("Adding pom for component'{}' (location '{}')", component.id, pomFile)
                        components.addToMapList(component.id as ModuleComponentIdentifier, pomFile)
                    }
                }
            }
        }
        logger.info("Pom component cache size=${pomModelResolver.componentCache.size}")
        pomModelResolver.componentCache.forEach { (componentId, file) ->
            logger.info("Pom Resolved componentId=$componentId")
            components.addToMapList(componentId, file)
        }
    }

    private fun resolvePom(pomModelResolver: PomDependencyModelResolver, pomFile: File) : Model? {
        val modelBuildingRequest = DefaultModelBuildingRequest()
        modelBuildingRequest.systemProperties = System.getProperties()
        modelBuildingRequest.modelSource = FileModelSource(pomFile)
        modelBuildingRequest.modelResolver = pomModelResolver
        return try {
            val modelBuilder = DefaultModelBuilderFactory().newInstance()
            val modelInterpolator = StringVisitorModelInterpolator()
            modelInterpolator.setUrlNormalizer(DefaultUrlNormalizer())
            modelInterpolator.setPathTranslator(DefaultPathTranslator())

            modelBuilder.setModelInterpolator(modelInterpolator)
            modelBuilder.setModelValidator(DefaultModelValidator())

            val result = modelBuilder.build(modelBuildingRequest)

            if (result.problems.isNotEmpty()) {
                result.problems.onEach {  logModelProblems(it) }
            }
            result.effectiveModel
        } catch (e: ModelBuildingException) {
            logger.error("${e.message}: ${e.problems}")
            null
        }
    }

    private fun collectIvyXmls(components: MutableMap<ModuleComponentIdentifier, MutableSet<File>>) {
        logger.trace("Collecting ivy xml files")
        val ivyArtifacts = project.dependencies.createArtifactResolutionQuery()
            .forComponents(components.keys)
            .withArtifacts(IvyModule::class.java, IvyDescriptorArtifact::class.java)
            .execute()

        ivyArtifacts.resolvedComponents.forEach { component ->
            val ivyXmls = component.getArtifacts(IvyDescriptorArtifact::class.java)
            if (ivyXmls.isNotEmpty()) {
                when (val ivyXmlArtifact = ivyXmls.first ()) {
                    is UnresolvedArtifactResult -> {
                        logger.error(
                            "Resolver was unable to resolve artifact '{}'",
                            ivyXmlArtifact.id,
                            ivyXmlArtifact.failure
                        )
                    }
                    is ResolvedArtifactResult -> {
                        val ivyXml = ivyXmlArtifact.file
                        logger.trace(
                            "Adding ivy artifact for component'${component.id}' (location '${ivyXml}')"
                        )
                        components.addToMapList(component.id as ModuleComponentIdentifier, ivyXml)
                    }
                }

            }
        }
    }

    private fun logModelProblems(problem: ModelProblem) {
        val message = "$problem.modelId: $problem.message"
        when (problem.severity) {
            ModelProblem.Severity.WARNING -> logger.info(message, problem.exception)
            ModelProblem.Severity.ERROR -> logger.error(message, problem.exception)
            ModelProblem.Severity.FATAL -> logger.error(message, problem.exception)
            null -> TODO()
        }
    }

    private fun moduleDirectory(ci: ModuleComponentIdentifier) : File =
        File(
            offlineRepoDir,
            "${ci.group.replace('.', '/')}/${ci.module}/${ci.version}")

    private fun getImperativeDepsArtifacts() : Set<ArtifactComponent> {
        val imperativeDepsList = listOf(
            // internals
            "org.gradle.kotlin.kotlin-dsl:org.gradle.kotlin.kotlin-dsl.gradle.plugin:2.1.7",
            "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.5.31",
            "org.apache.maven:maven-model-builder:3.8.4",

            // externals
            "com.android.tools.build:gradle:7.2.0-beta02",
            "com.android.tools.lint:lint-gradle:30.2.0-beta02",
            "com.gradle:gradle-enterprise-gradle-plugin:3.8.1",
        )
        project.repositories.gradlePluginPortal()
        project.repositories.mavenCentral()
        project.repositories.maven("https://plugins.gradle.org/m2/")
        project.repositories.google()
        val artifacts = project.configurations.detachedConfiguration(
            *imperativeDepsList.map { project.dependencies.create(it) }.toTypedArray()
        ).resolveToAllRecurseArtifacts()
//        artifacts.forEach { artifact ->
//            logger.info("Imperative artifact: id=${artifact.id}, files=${artifact.files}")
//        }
		return artifacts
    }

    private fun Dependency.artifactToComponents() : Set<ArtifactComponent> {
        return if (this is ExternalModuleDependency) {
            project.configurations.detachedConfiguration(this).resolveToAllRecurseArtifacts()
        } else {
            setOf()
        }
    }

    private fun <K, V> MutableMap<K, MutableSet<V>>.addToMapList(key: K, newelem: V) {
        val elem = get(key)
        if (elem != null) {
            elem.add(newelem)
        } else {
            put(key, mutableSetOf(newelem))
        }
    }
    private fun <A> MutableMap<ModuleComponentIdentifier, MutableSet<File>>.addFromComponentArtifacts(
        component: ComponentArtifactsResult,
        artifactClass: Class<A>) where A: Artifact {
        val sources: Set<ArtifactResult>  = component.getArtifacts(artifactClass)
        sources.forEach {
            if (it is ResolvedArtifactResult) {
                logger.trace("Adding sources for component'${component.id}' (location '${it.file}')")
                addToMapList(component.id as ModuleComponentIdentifier, it.file)
            }
        }
    }

    private fun ResolvedArtifact.toArtifactComponent(): ArtifactComponent {
        val componentId = with(moduleVersion.id) {
            DefaultModuleComponentIdentifier.newId(
                DefaultModuleIdentifier.newId(group, name),
                version
            )
        }
        return ArtifactComponent(componentId, mutableSetOf(file))
    }

    private fun Configuration.resolveToAllRecurseArtifacts() : Set<ArtifactComponent> =
        resolvedConfiguration.resolvedArtifacts.map { artifact ->
            artifact.toArtifactComponent()
        }.toSet() +
        resolvedConfiguration.firstLevelModuleDependencies.map {
            it.allModuleArtifacts.map { artifact ->
                artifact.toArtifactComponent()
            }.toSet()
        }.flatten().toSet()

}


