package com.netfalo.gradle.plugin;

import org.gradle.api.Project
import org.gradle.api.Plugin

class RamlPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('raml', RamlPluginExtension, project)

        def verifyRaml = project.tasks.create('verifyRaml', VerifyRamlTask)
        verifyRaml.dependsOn("build")

        def generateRaml = project.tasks.create('generateRaml', GenerateRamlTask) {
            defaultMediaType = extension.defaultMediaTypeProvider
            dependencyPackagesList = extension.dependencyPackagesListProvider
            ignoredList = extension.ignoredListProvider
            javaDocPath = extension.javaDocPathProvider

            outputRamlFilePath = extension.outputRamlFilePathProvider
            restBasePath = extension.restBasePathProvider
            restrictOnMediaType = extension.restrictOnMediaTypeProvider
            includeGlobalMediaType = extension.includeGlobalMediaTypeProvider
            createPathIfMissing = extension.createPathIfMissingProvider
            removeOldOutput = extension.removeOldOutputProvider
            version = extension.versionProvider
        }
        generateRaml.dependsOn("build")
    }
}
