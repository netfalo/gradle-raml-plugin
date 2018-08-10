package com.netfalo.gradle.plugin

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier
import com.phoenixnap.oss.ramlapisync.generation.rules.RamlLoader
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot
import com.phoenixnap.oss.ramlapisync.style.RamlStyleChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.ActionSecurityResponseChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.RequestBodySchemaStyleChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceCollectionPluralisationChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceUrlStyleChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseBodySchemaStyleChecker
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseCodeDefinitionStyleChecker
import com.phoenixnap.oss.ramlapisync.verification.Issue
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionContentTypeChecker
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionExistenceChecker
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionQueryParameterChecker
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionResponseBodySchemaChecker
import com.phoenixnap.oss.ramlapisync.verification.checkers.ResourceExistenceChecker
import org.gradle.api.tasks.TaskAction
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class VerifyRamlTask extends RamlTask {
    private boolean checkRamlAgainstImplementation = project.extensions.raml.verify.checkRamlAgainstImplementation
    private String uriPrefixToIgnore = project.extensions.raml.verify. uriPrefixToIgnore
    private boolean performStyleChecks = project.extensions.raml.verify. performStyleChecks
    private boolean checkForResourceExistence = project.extensions.raml.verify. checkForResourceExistence
    private boolean checkForActionExistence = project.extensions.raml.verify. checkForActionExistence
    private boolean checkForActionContentType = project.extensions.raml.verify. checkForActionContentType
    private boolean checkForQueryParameterExistence = project.extensions.raml.verify. checkForQueryParameterExistence
    private boolean checkForPluralisedResourceNames = project.extensions.raml.verify. checkForPluralisedResourceNames
    private boolean checkForSpecialCharactersInResourceNames = project.extensions.raml.verify. checkForSpecialCharactersInResourceNames
    private boolean checkForDefinitionOf40xResponseInSecuredResource = project.extensions.raml.verify. checkForDefinitionOf40xResponseInSecuredResource
    private String checkForSchemaInSuccessfulResponseBody = project.extensions.raml.verify.checkForSchemaInSuccessfulResponseBody
    private boolean checkForDefinitionOfErrorCodes = project.extensions.raml.verify.checkForDefinitionOfErrorCodes
    private String checkForSchemaInRequestBody = project.extensions.raml.verify.checkForSchemaInRequestBody
    private boolean checkForDefinitionOf404ResponseInGetRequest = project.extensions.raml.verify.checkForDefinitionOf404ResponseInGetRequest
    private boolean checkForResponseBodySchema = project.extensions.raml.verify.checkForResponseBodySchema
    private boolean breakBuildOnWarnings = project.extensions.raml.verify.breakBuildOnWarnings
    private boolean logWarnings = project.extensions.raml.verify.logWarnings
    private boolean logErrors = project.extensions.raml.verify.logErrors

    final private static String VERSION = "1"
    private static Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class)

    @TaskAction
    def verifyRaml() {
        prepareRaml()
        Class<?>[] classArray = new Class[annotatedClasses.size()]
        classArray = this.annotatedClasses.toArray(classArray)

        //Lets use the base folder if supplied or default to relative scanning
        File targetPath
        if (StringUtils.hasText(project.extensions.raml.javaDocPath)) {
            targetPath = new File(project.extensions.raml.javaDocPath)
        } else if (project.getProjectDir().getParentFile() != null) {
            targetPath = project.getProjectDir().getParentFile()
        } else {
            targetPath = project.getProjectDir()
        }

        List<RamlChecker> checkers = new ArrayList<>()
        List<RamlActionVisitorCheck> actionCheckers = new ArrayList<>()
        List<RamlResourceVisitorCheck> resourceCheckers = new ArrayList<>()
        RamlRoot implementedRaml = null

        if (checkRamlAgainstImplementation) {
            ResourceParser scanner = new SpringMvcResourceParser(targetPath, VERSION, ResourceParser.CATCH_ALL_MEDIA_TYPE, false)
            RamlGenerator ramlGenerator = new RamlGenerator(scanner)

            // Process the classes selected and build Raml model
            ramlGenerator.generateRamlForClasses(project.name, VERSION, "/", classArray, this.documents)
            implementedRaml = ramlGenerator.getRaml()

            if (checkForResourceExistence) {
                checkers.add(new ResourceExistenceChecker())
            }
            if (checkForActionExistence) {
                resourceCheckers.add(new ActionExistenceChecker())
            }
            if (checkForQueryParameterExistence) {
                actionCheckers.add(new ActionQueryParameterChecker())
            }
            if (checkForActionContentType) {
                actionCheckers.add(new ActionContentTypeChecker())
            }
            if(checkForResponseBodySchema) {
                actionCheckers.add(new ActionResponseBodySchemaChecker())
            }
        }

        List<RamlStyleChecker> styleCheckers = new ArrayList<>()

        if (performStyleChecks) {
            if (checkForPluralisedResourceNames) {
                styleCheckers.add(new ResourceCollectionPluralisationChecker())
            }
            if (checkForDefinitionOf40xResponseInSecuredResource) {
                styleCheckers.add(new ActionSecurityResponseChecker())
            }
            if (checkForSpecialCharactersInResourceNames) {
                styleCheckers.add(new ResourceUrlStyleChecker())
            }
            if (StringUtils.hasText(checkForSchemaInRequestBody)) {
                styleCheckers.add(new RequestBodySchemaStyleChecker(checkForSchemaInRequestBody))
            }
            if (StringUtils.hasText(checkForSchemaInSuccessfulResponseBody)) {
                styleCheckers.add(new ResponseBodySchemaStyleChecker(checkForSchemaInSuccessfulResponseBody))
            }
            MultiValueMap<String, HttpStatus> statusChecks = new LinkedMultiValueMap<>()
            if (checkForDefinitionOf404ResponseInGetRequest) {
                statusChecks.add(HttpMethod.GET.name(), HttpStatus.NOT_FOUND)
            }
            if (checkForDefinitionOfErrorCodes) {
                statusChecks.add(HttpMethod.PUT.name(), HttpStatus.BAD_REQUEST)
                statusChecks.add(HttpMethod.POST.name(), HttpStatus.BAD_REQUEST)
                statusChecks.add(HttpMethod.PATCH.name(), HttpStatus.BAD_REQUEST)

                statusChecks.add(HttpMethod.GET.name(), HttpStatus.INTERNAL_SERVER_ERROR)
                statusChecks.add(HttpMethod.PATCH.name(), HttpStatus.INTERNAL_SERVER_ERROR)
                statusChecks.add(HttpMethod.PUT.name(), HttpStatus.INTERNAL_SERVER_ERROR)
                statusChecks.add(HttpMethod.POST.name(), HttpStatus.INTERNAL_SERVER_ERROR)
                statusChecks.add(HttpMethod.DELETE.name(), HttpStatus.INTERNAL_SERVER_ERROR)
            }

            if (!statusChecks.isEmpty()) {
                styleCheckers.add(new ResponseCodeDefinitionStyleChecker(statusChecks))
            }
        }

        RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(project.extensions.raml.verify.ramlFile)

        RamlVerifier verifier = new RamlVerifier(loadRamlFromFile, implementedRaml, checkers, actionCheckers, resourceCheckers, styleCheckers, StringUtils.hasText(uriPrefixToIgnore) ? uriPrefixToIgnore : null)
        if (verifier.hasWarnings() && logWarnings) {
            for (Issue issue : verifier.getWarnings()) {
                logger.warn(issue.toString())
            }
        }
        if (verifier.hasErrors()) {
            if (logErrors) {
                for (Issue issue : verifier.getErrors()) {
                    logger.error(issue.toString())
                }
            }
            throw new IllegalStateException("Errors found when comparing RAML to Spring MVC Implementation")
        }
        if(verifier.hasWarnings() && breakBuildOnWarnings) {
            throw new IllegalStateException("Warnings found when comparing RAML to Spring MVC Implementation and build is set to break on Warnings")
        }
    }
}
