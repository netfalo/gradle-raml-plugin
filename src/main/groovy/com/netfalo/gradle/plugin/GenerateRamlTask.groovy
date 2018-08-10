package com.netfalo.gradle.plugin

import org.gradle.api.tasks.TaskAction

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser

import org.springframework.util.StringUtils

class GenerateRamlTask extends RamlTask {
    private String defaultMediaType = project.extensions.raml.defaultMediaType
    private String javaDocPath = project.extensions.raml.javaDocPath

    private String outputRamlFilePath = project.extensions.raml.generate.outputRamlFilePath
    private String restBasePath = project.extensions.raml.generate.restBasePath
    private Boolean removeOldOutput = project.extensions.raml.generate.removeOldOutput
    private Boolean restrictOnMediaType = project.extensions.raml.generate.restrictOnMediaType
    private Boolean includeGlobalMediaType = project.extensions.raml.generate.includeGlobalMediaType
    private Boolean createPathIfMissing = project.extensions.raml.generate.createPathIfMissing
    private String version = project.extensions.raml.generate.version

    @TaskAction
    def generateRaml() {
        prepareRaml()

        Class<?>[] classArray = new Class[annotatedClasses.size()]
        classArray = annotatedClasses.toArray(classArray)

        //Lets use the base folder if supplied or default to relative scanning
        File targetPath
        if (StringUtils.hasText(javaDocPath)) {
            targetPath = new File(javaDocPath)
        } else if (project.getProjectDir().getParentFile() != null) {
            targetPath = project.getProjectDir().getParentFile()
        } else {
            targetPath = project.getProjectDir()
        }

        ResourceParser scanner = new SpringMvcResourceParser(targetPath, project.version.toString(), defaultMediaType, restrictOnMediaType)
        RamlGenerator ramlGenerator = new RamlGenerator(scanner)

        // Process the classes selected and build Raml model
        ramlGenerator.generateRamlForClasses(project.name, version, restBasePath, classArray, documents)

        //Add a global media type
        if (includeGlobalMediaType) {
            ramlGenerator.setRamlMediaType(defaultMediaType)
        }

        // Extract RAML as a string and save to file
        ramlGenerator.outputRamlToFile(getFullRamlOutputPath(), createPathIfMissing, removeOldOutput)
    }

    private String getFullRamlOutputPath() {
        return project.getBuildDir().getAbsolutePath() + File.separator + outputRamlFilePath
    }
}
