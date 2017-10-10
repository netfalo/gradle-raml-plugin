package com.netfalo.gradle.plugin

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class RamlPluginTest {
    @Test
    void ramlPluginAddsGenerateRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertTrue(project.tasks.generateRaml instanceof GenerateRamlTask)
    }

    @Test
    void ramlPluginAddsVerifyRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertTrue(project.tasks.verifyRaml instanceof VerifyRamlTask)
    }
}
