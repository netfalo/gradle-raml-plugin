package com.netfalo.gradle.plugin;

import org.gradle.api.Project
import org.gradle.api.Plugin

class RamlPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('raml', type: RamlTask)
    }
}
