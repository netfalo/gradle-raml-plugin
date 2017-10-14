package com.netfalo.gradle.plugin

import com.google.common.base.Strings
import com.google.common.reflect.ClassPath
import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController

import java.lang.annotation.Annotation

class RamlTask extends DefaultTask {
    final private Class<? extends Annotation>[] supportedClassAnnotations = getSupportedClassAnnotations()

    final private PropertyState<String> defaultMediaType = project.property(String)
    final private PropertyState<List<String>> dependencyPackagesList = (PropertyState<List<String>>) (Object) project.property(List.class)
    final private PropertyState<List<String>> ignoredList = (PropertyState<List<String>>) (Object) project.property(List.class)
    final private PropertyState<String> javaDocPath = project.property(String)

    String documentationSuffix = "-doc.md"
    private Set<ApiDocumentMetadata> documents = new LinkedHashSet<>()
    private List<Class<?>> annotatedClasses = new ArrayList<>()

    def prepareRaml() {
        ClassLoaderUtils.addLocationsToClassLoader(project)
        List<String> targetPacks = ClassLoaderUtils.loadPackages(project)
        if (!dependencyPackagesList.get().isEmpty()) {
            targetPacks.addAll(dependencyPackagesList.get())
        }

        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader())
        for (String pack : targetPacks) {
            scanPack(pack, classPath)
        }

        for (ClassPath.ResourceInfo resourceInfo : classPath.getResources()) {
            if (resourceInfo.getResourceName().endsWith(documentationSuffix)) {
                try {
                    documents.add(new ApiDocumentMetadata(resourceInfo, documentationSuffix))
                    logger.info("Adding Documentation File " + resourceInfo.getResourceName())
                } catch (Throwable ex) {
                    logger.warn("Skipping Resource: Unable to load" + resourceInfo.getResourceName(), ex)
                }
            }
        }

        ClassLoaderUtils.restoreOriginalClassLoader()

    }

    def scanPack(String pack, ClassPath classPath) {
        if (Strings.isNullOrEmpty(pack)) {
            ClassLoaderUtils.restoreOriginalClassLoader()
        }

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(pack)) {
            try {
                Class<?> c = classInfo.load()

                if (!ignoredList.contains(c.getPackage().getName()) && !ignoredList.contains(c.getName())) {
                    scanClass(c)
                }
            } catch (Throwable ex) {
                this.getLog().warn("Skipping Class: Unable to load" + classInfo.getName(), ex)
            }
        }
    }

    def scanClass(Class<?> c) {
        for (Class<? extends Annotation> cAnnotation : supportedClassAnnotations) {
            if (c.isAnnotationPresent(cAnnotation)) {
                annotatedClasses.add(c)
            }
        }
    }

    static def getSupportedClassAnnotations() {
        return [Controller.class, RestController.class]
    }

    @Input
    String getDefaultMediaType() {
        defaultMediaType.get()
    }

    void setDefaultMediaType(String defaultMediaType) {
        this.defaultMediaType.set(defaultMediaType)
    }

    void setDefaultMediaType(Provider<String> defaultMediaType) {
        this.defaultMediaType.set(defaultMediaType)
    }

    @Input
    List<String> getDependencyPackagesList() {
        dependencyPackagesList.get()
    }

    void setDependencyPackagesList(List<String> dependencyPackagesList) {
        this.dependencyPackagesList.set(dependencyPackagesList)
    }

    void setDependencyPackagesList(Provider<List<String>> dependencyPackagesList) {
        this.dependencyPackagesList.set(dependencyPackagesList)
    }

    @Input
    List<String> getIgnoredList() {
        ignoredList.get()
    }

    void setIgnoredList(List<String> ignoredList) {
        this.ignoredList.set(ignoredList)
    }

    void setIgnoredList(Provider<List<String>> ignoredList) {
        this.ignoredList.set(ignoredList)
    }

    @Input
    String getJavaDocPath() {
        javaDocPath.get()
    }

    void getJavaDocPath(String javaDocPath) {
        this.javaDocPath.set(javaDocPath)
    }

    void setJavaDocPath(Provider<String> javaDocPath) {
        this.javaDocPath.set(javaDocPath)
    }

    protected Set<ApiDocumentMetadata> getDocuments() {
        documents
    }

    protected List<Class<?>> getAnnotatedClasses() {
        annotatedClasses
    }
}
