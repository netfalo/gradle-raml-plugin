package com.netfalo.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider

class RamlPluginExtension {
    final private PropertyState<String> defaultMediaType
    final private PropertyState<List<String>> dependencyPackagesList
    final private PropertyState<List<String>> ignoredList
    final private PropertyState<String> javaDocPath

    final private PropertyState<String> outputRamlFilePath
    final private PropertyState<String> restBasePath
    final private PropertyState<Boolean> restrictOnMediaType
    final private PropertyState<Boolean> includeGlobalMediaType
    final private PropertyState<Boolean> createPathIfMissing
    final private PropertyState<Boolean> removeOldOutput
    final private PropertyState<String> version

    RamlPluginExtension(Project project) {
        defaultMediaType = project.property(String)
        defaultMediaType.set("application/json")

        dependencyPackagesList = (PropertyState<List<String>>) (Object) project.property(List.class)
        dependencyPackagesList.set(new ArrayList<String>())

        ignoredList = (PropertyState<List<String>>) (Object) project.property(List.class)
        ignoredList.set(new ArrayList<String>())

        javaDocPath = project.property(String)
        javaDocPath.set(new String())

        outputRamlFilePath = project.property(String)
        outputRamlFilePath.set('api.raml')

        restBasePath = project.property(String)
        restBasePath.set(new String())

        restrictOnMediaType = project.property(Boolean)
        restrictOnMediaType.set(false)

        includeGlobalMediaType = project.property(Boolean)
        includeGlobalMediaType.set(false)

        createPathIfMissing = project.property(Boolean)
        createPathIfMissing.set(false)

        removeOldOutput = project.property(Boolean)
        removeOldOutput.set(false)

        version = project.property(String)
        version.set(project.version.toString())
    }

    String getDefaultMediaType() {
        defaultMediaType.get()
    }

    Provider<String> getDefaultMediaTypeProvider() {
        defaultMediaType
    }

    void setDefaultMediaType(String defaultMediaType) {
        this.defaultMediaType.set(defaultMediaType)
    }

    List<String> getDependencyPackagesList() {
        dependencyPackagesList.get()
    }

    Provider<List> getDependencyPackagesListProvider() {
        dependencyPackagesList
    }

    void setDependencyPackagesList(List<String> dependencyPackagesList) {
        this.dependencyPackagesList.set(dependencyPackagesList)
    }

    List<String> getIgnoredList() {
        ignoredList.get()
    }

    Provider<List> getIgnoredListProvider() {
        ignoredList
    }

    void setIgnoredList(List<String> ignoredList) {
        this.ignoredList.set(ignoredList)
    }

    String getJavaDocPath() {
        javaDocPath.get()
    }

    Provider<String> getJavaDocPathProvider() {
        javaDocPath
    }

    void setJavaDocPath(String javaDocPath) {
        this.javaDocPath.set(javaDocPath)
    }

    String getOutputRamlFilePath() {
        outputRamlFilePath.get()
    }

    Provider<String> getOutputRamlFilePathProvider() {
        outputRamlFilePath
    }

    void setOutputRamlFilePath(String outputRamlFilePath) {
        this.outputRamlFilePath.set(outputRamlFilePath)
    }

    String getRestBasePath() {
        restBasePath.get()
    }

    Provider<String> getRestBasePathProvider() {
        restBasePath
    }

    void setRestBasePath(String restBasePath) {
        this.restBasePath.set(restBasePath)
    }

    Provider<Boolean> getRestrictOnMediaTypeProvider() {
        restrictOnMediaType
    }

    void setRestrictOnMediaType(Boolean restrictOnMediaType) {

    }

    Boolean getRestrictOnMediaType() {
        restrictOnMediaType.get()
    }

    Boolean getIncludeGlobalMediaType() {
        includeGlobalMediaType.get()
    }

    Provider<Boolean> getIncludeGlobalMediaTypeProvider() {
        includeGlobalMediaType
    }

    void setIncludeGlobalMediaTypeProvider(Boolean includeGlobalMediaType) {
        this.includeGlobalMediaType.set(includeGlobalMediaType)
    }

    Boolean getCreatePathIfMissing() {
        createPathIfMissing.get()
    }

    Provider<Boolean> getCreatePathIfMissingProvider() {
        createPathIfMissing
    }

    void setRestBasePath(Boolean createPathIfMissing) {
        this.createPathIfMissing.set(createPathIfMissing)
    }

    Boolean getRemoveOldOutput() {
        removeOldOutput.get()
    }

    Provider<Boolean> getRemoveOldOutputProvider() {
        removeOldOutput
    }

    void setRemoveOldOutput(Boolean removeOldOutput) {
        this.removeOldOutput.set(removeOldOutput)
    }

    String getVersion() {
        version.get()
    }

    Provider<String> getVersionProvider() {
        version
    }

    void setVersion(String version) {
        this.version.set(version)
    }
}
