package com.netfalo.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

class RamlPluginTest {
    @Test
    void ramlPluginAddsGenerateRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertThat(project.tasks.generateRaml, is(instanceOf(GenerateRamlTask)))
    }

    @Test
    void ramlPluginAddsVerifyRamlTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.netfalo.raml'

        assertThat(project.tasks.verifyRaml, is(instanceOf(VerifyRamlTask)))
    }

}
