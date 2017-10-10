package com.netfalo.gradle.plugin

import com.google.common.base.Strings
import com.google.common.reflect.ClassPath
import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata
import org.gradle.api.DefaultTask
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController

import java.lang.annotation.Annotation

class RamlTask extends DefaultTask {
    List<String> ignoredList = new ArrayList<>()
    List<String> dependencyPackagesList = new ArrayList<>()
    Class<? extends Annotation>[] supportedClassAnnotations = getSupportedClassAnnotations()
    String documentationSuffix = "-doc.md"
    Set<ApiDocumentMetadata> documents = new LinkedHashSet<>()
    List<Class<?>> annotatedClasses = new ArrayList<>()
    final static String version = "1"
    String defaultMediaType = "application/json"
    String javaDocPath = ""

    def prepareRaml() {
        ClassLoaderUtils.addLocationsToClassLoader(project)
        List<String> targetPacks = ClassLoaderUtils.loadPackages(project)
        if (dependencyPackagesList != null && !dependencyPackagesList.isEmpty()) {
            targetPacks.addAll(dependencyPackagesList)
        }

        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader())
        for (String pack : targetPacks) {
            scanPack(pack, classPath)
        }

        for (ClassPath.ResourceInfo resourceInfo : classPath.getResources()) {
            if (resourceInfo.getResourceName().endsWith(documentationSuffix)) {
                try {
                    documents.add(new ApiDocumentMetadata(resourceInfo, documentationSuffix))
                    this.getLog().info("Adding Documentation File " + resourceInfo.getResourceName())
                } catch (Throwable ex) {
                    this.getLog().warn("Skipping Resource: Unable to load" + resourceInfo.getResourceName(), ex)
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
}
