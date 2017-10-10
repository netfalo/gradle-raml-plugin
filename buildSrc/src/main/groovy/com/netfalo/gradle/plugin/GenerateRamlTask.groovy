package com.netfalo.gradle.plugin

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser
import org.gradle.api.tasks.TaskAction
import org.springframework.util.StringUtils

class GenerateRamlTask extends RamlTask {

    boolean restrictOnMediaType = false
    String restBasePath
    boolean includeGlobalMediaType = false
    boolean createPathIfMissing = false
    boolean removeOldOutput = false
    String outputRamlFilePath = ""

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

        ResourceParser scanner = new SpringMvcResourceParser(targetPath, version, defaultMediaType, restrictOnMediaType)
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
}
