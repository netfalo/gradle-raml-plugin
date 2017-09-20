package com.netfalo.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class RamlTask extends DefaultTask {
    String greeting = 'hello from RamlTask'

    @TaskAction
    def raml() {
        println greeting
    }
}
