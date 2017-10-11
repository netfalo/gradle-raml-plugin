package com.netfalo.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat

class VerifyRamlTaskTest {
    @Test
    void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('verifyRaml', type: VerifyRamlTask)
        assertThat(task, instanceOf(VerifyRamlTask))
    }
}
