package com.netfalo.gradle.plugin

import com.google.common.base.Strings
import com.google.common.reflect.ClassPath
import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata
import org.gradle.api.DefaultTask
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController

import java.lang.annotation.Annotation

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RamlTask extends DefaultTask {
    private static Logger logger = LoggerFactory.getLogger(RamlTask.class)
    final private Class<? extends Annotation>[] supportedClassAnnotations = getSupportedClassAnnotations()

    private String documentationSuffix = "-doc.md"
    private Set<ApiDocumentMetadata> documents = new LinkedHashSet<>()
    protected List<Class<?>> annotatedClasses = new ArrayList<>()

    def prepareRaml() {
        ClassLoaderUtils.addLocationsToClassLoader(project)
        List<String> targetPacks = ClassLoaderUtils.loadPackages(project)
        if (!project.extensions.raml.dependencyPackagesList.isEmpty()) {
            targetPacks.addAll(project.extensions.raml.dependencyPackagesList)
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
        def ignoredList = project.extensions.raml.ignoredList

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
                logger.warn("Skipping Class: Unable to load '" + classInfo.getName() + "'")
                logger.debug("exception", ex)
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

    protected Set<ApiDocumentMetadata> getDocuments() {
        documents
    }

    protected List<Class<?>> getAnnotatedClasses() {
        annotatedClasses
    }
}
