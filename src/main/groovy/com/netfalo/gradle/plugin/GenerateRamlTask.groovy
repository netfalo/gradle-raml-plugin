package com.netfalo.gradle.plugin

import org.gradle.api.tasks.TaskAction

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser

import org.springframework.util.StringUtils

class GenerateRamlTask extends RamlTask {
    private boolean restrictOnMediaType = false
    private boolean includeGlobalMediaType = false
    private boolean createPathIfMissing = false
    private boolean removeOldOutput = false

    private String outputRamlFilePath = "api.raml"
    private String restBasePath

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
        ramlGenerator
                .generateRamlForClasses(project.name, project.version.toString(), restBasePath, classArray, documents)

        //Add a global media type
        if (includeGlobalMediaType) {
            ramlGenerator.setRamlMediaType(defaultMediaType)
        }

        // Extract RAML as a string and save to file
        ramlGenerator.outputRamlToFile(getFullRamlOutputPath(), createPathIfMissing, removeOldOutput)
    }

    def getFullRamlOutputPath() {
        // must get basedir from project to ensure that correct basedir is used when building from parent
        return project.getProjectDir().getAbsolutePath() + File.separator + outputRamlFilePath
    }

    String getOutputRamlFilePath() {
        outputRamlFilePath
    }

    void setOutputRamlFilePath(String outputRamlFilePath) {
        this.outputRamlFilePath = outputRamlFilePath
    }

    String getRestBasePath() {
        restBasePath
    }

    void setRestBasePath(String restBasePath) {
        this.restBasePath = restBasePath
    }

    boolean getRestrictOnMediaType() {
        restrictOnMediaType
    }

    void setRestrictOnMediaType(boolean restrictOnMediaType) {
        this.restrictOnMediaType = restrictOnMediaType
    }

    boolean getIncludeGlobalMediaType() {
        includeGlobalMediaType
    }

    void setIncludeGlobalMediaType(boolean includeGlobalMediaType) {
        this.includeGlobalMediaType = includeGlobalMediaType
    }

    boolean getCreatePathIfMissing() {
        createPathIfMissing
    }

    void setCreatePathIfMissing(boolean createPathIfMissing) {
        this.createPathIfMissing = createPathIfMissing
    }

    boolean getRemoveOldOutput() {
        removeOldOutput
    }

    void setRemoveOldOutput(boolean removeOldOutput) {
        this.removeOldOutput = removeOldOutput
    }

}
