package com.netfalo.gradle.plugin

import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser

import org.springframework.util.StringUtils

class GenerateRamlTask extends RamlTask {
    final private PropertyState<Boolean> restrictOnMediaType = project.property(Boolean)
    final private PropertyState<Boolean> includeGlobalMediaType = project.property(Boolean)
    final private PropertyState<Boolean> createPathIfMissing = project.property(Boolean)
    final private PropertyState<Boolean> removeOldOutput = project.property(Boolean)

    final private PropertyState<String> outputRamlFilePath = project.property(String)
    final private PropertyState<String> restBasePath = project.property(String)

    final private PropertyState<String> version = project.property(String)

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

        ResourceParser scanner = new SpringMvcResourceParser(targetPath, project.version.toString(), defaultMediaType, restrictOnMediaType.get())
        RamlGenerator ramlGenerator = new RamlGenerator(scanner)
        // Process the classes selected and build Raml model
        ramlGenerator
                .generateRamlForClasses(project.name, version.get(), restBasePath.get(), classArray, documents)

        //Add a global media type
        if (includeGlobalMediaType.get()) {
            ramlGenerator.setRamlMediaType(defaultMediaType)
        }

        // Extract RAML as a string and save to file
        ramlGenerator.outputRamlToFile(getFullRamlOutputPath(), createPathIfMissing.get(), removeOldOutput.get())
    }

    private String getFullRamlOutputPath() {
        return project.getBuildDir().getAbsolutePath() + File.separator + outputRamlFilePath.get()
    }

    @Input
    String getOutputRamlFilePath() {
        outputRamlFilePath
    }

    void setOutputRamlFilePath(String outputRamlFilePath) {
        this.outputRamlFilePath.set(outputRamlFilePath)
    }

    void setOutputRamlFilePath(Provider<String> outputRamlFilePath) {
        this.outputRamlFilePath.set(outputRamlFilePath)
    }

    @Input
    String getRestBasePath() {
        restBasePath
    }

    void setRestBasePath(String restBasePath) {
        this.restBasePath.set(restBasePath)
    }

    void setRestBasePath(Provider<String> restBasePath) {
        this.restBasePath.set(restBasePath)
    }

    @Input
    boolean getRestrictOnMediaType() {
        restrictOnMediaType
    }

    void setRestrictOnMediaType(boolean restrictOnMediaType) {
        this.restrictOnMediaType.set(restrictOnMediaType)
    }

    void setRestrictOnMediaType(Provider<Boolean> restrictOnMediaType) {
        this.restrictOnMediaType.set(restrictOnMediaType)
    }

    @Input
    boolean getIncludeGlobalMediaType() {
        includeGlobalMediaType
    }

    void setIncludeGlobalMediaType(boolean includeGlobalMediaType) {
        this.includeGlobalMediaType.set(includeGlobalMediaType)
    }

    void setIncludeGlobalMediaType(Provider<Boolean> includeGlobalMediaType) {
        this.includeGlobalMediaType.set(includeGlobalMediaType)
    }

    @Input
    boolean getCreatePathIfMissing() {
        createPathIfMissing
    }

    void setCreatePathIfMissing(boolean createPathIfMissing) {
        this.createPathIfMissing.set(createPathIfMissing)
    }

    void setCreatePathIfMissing(Provider<Boolean> createPathIfMissing) {
        this.createPathIfMissing.set(createPathIfMissing)
    }

    @Input
    boolean getRemoveOldOutput() {
        removeOldOutput
    }

    void setRemoveOldOutput(boolean removeOldOutput) {
        this.removeOldOutput.set(removeOldOutput)
    }

    void setRemoveOldOutput(Provider<Boolean> removeOldOutput) {
        this.removeOldOutput.set(removeOldOutput)
    }

    @Input
    String getVersion() {
        version
    }

    void setVersion(String version) {
        this.version.set(version)
    }

    void setVersion(Provider<String> version) {
        this.version.set(version)
    }

}
