package com.netfalo.gradle.plugin;

import org.gradle.api.Project
import org.gradle.api.Plugin

class RamlPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.add('raml', RamlPluginExtension)
        project.extensions.raml.extensions.add('verify', VerifyRamlExtension)
        project.extensions.raml.extensions.add('generate', GenerateRamlExtension)

        def verifyRaml = project.tasks.create('verifyRaml', VerifyRamlTask)
        verifyRaml.dependsOn("build")

        def generateRaml = project.tasks.create('generateRaml', GenerateRamlTask)
        generateRaml.dependsOn("build")
    }
}
