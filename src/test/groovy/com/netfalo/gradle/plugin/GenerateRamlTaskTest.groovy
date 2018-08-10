package com.netfalo.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.MatcherAssert.assertThat

class GenerateRamlTaskTest {
    @Test
    void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.extensions.add('raml', RamlPluginExtension)
        project.extensions.raml.extensions.add('generate', GenerateRamlExtension)

        def task = project.task('generateRaml', type: GenerateRamlTask)
        assertThat(task, instanceOf(GenerateRamlTask))
    }
}
