package com.netfalo.gradle.plugin;

import org.gradle.api.Project
import org.gradle.api.Plugin

class RamlPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('verifyRaml', type: VerifyRamlTask)
        project.task('generateRaml', type: GenerateRamlTask)
    }
}
