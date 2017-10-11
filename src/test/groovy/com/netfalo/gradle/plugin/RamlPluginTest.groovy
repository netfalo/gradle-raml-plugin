package com.netfalo.gradle.plugin

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

import static org.hamcrest.CoreMatchers.hasItem
import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

class RamlPluginTest {
    @Test
    void ramlPluginAddsGenerateRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertThat(project.tasks.generateRaml, instanceOf(GenerateRamlTask))
    }

    @Test
    void ramlPluginAddsVerifyRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertThat(project.tasks.verifyRaml, instanceOf(VerifyRamlTask))
    }
}
