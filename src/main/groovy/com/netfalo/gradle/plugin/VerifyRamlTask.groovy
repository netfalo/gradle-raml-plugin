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

class VerifyRamlTask extends RamlTask {
    boolean checkRamlAgainstImplementation = true
    String uriPrefixToIgnore
    boolean performStyleChecks = true
    boolean checkForResourceExistence = true
    boolean checkForActionExistence = true
    boolean checkForActionContentType = true
    boolean checkForQueryParameterExistence = true
    boolean checkForPluralisedResourceNames = true
    boolean checkForSpecialCharactersInResourceNames = true
    boolean checkForDefinitionOf40xResponseInSecuredResource = true
    String checkForSchemaInSuccessfulResponseBody
    boolean checkForDefinitionOfErrorCodes = false
    String checkForSchemaInRequestBody
    boolean checkForDefinitionOf404ResponseInGetRequest = false
    boolean checkForResponseBodySchema = false
    boolean breakBuildOnWarnings = false
    boolean logWarnings = true
    boolean logErrors = true

    String ramlToVerifyPath
    final private static String VERSION = "1"

    @TaskAction
    def verifyRaml() {
        prepareRaml()

        Class<?>[] classArray = new Class[annotatedClasses.size()]
        classArray = this.annotatedClasses.toArray(classArray)

        //Lets use the base folder if supplied or default to relative scanning
        File targetPath
        if (StringUtils.hasText(javaDocPath)) {
            targetPath = new File(javaDocPath)
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
            ramlGenerator.generateRamlForClasses(project. project.name, VERSION, "/", classArray, this.documents)
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


        RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(ramlToVerifyPath)

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

    String getRamlToVerifyPath() {
        ramlToVerifyPath
    }

    def setRamlToVerifyPath(String ramlToVerifyPath) {
        this.ramlToVerifyPath = ramlToVerifyPath
    }

    boolean getCheckRamlAgainstImplementation() {
        return checkRamlAgainstImplementation
    }

    void setCheckRamlAgainstImplementation(boolean checkRamlAgainstImplementation) {
        this.checkRamlAgainstImplementation = checkRamlAgainstImplementation
    }

    String getUriPrefixToIgnore() {
        return uriPrefixToIgnore
    }

    void setUriPrefixToIgnore(String uriPrefixToIgnore) {
        this.uriPrefixToIgnore = uriPrefixToIgnore
    }

    boolean getPerformStyleChecks() {
        return performStyleChecks
    }

    void setPerformStyleChecks(boolean performStyleChecks) {
        this.performStyleChecks = performStyleChecks
    }

    boolean getCheckForResourceExistence() {
        return checkForResourceExistence
    }

    void setCheckForResourceExistence(boolean checkForResourceExistence) {
        this.checkForResourceExistence = checkForResourceExistence
    }

    boolean getCheckForActionExistence() {
        return checkForActionExistence
    }

    void setCheckForActionExistence(boolean checkForActionExistence) {
        this.checkForActionExistence = checkForActionExistence
    }

    boolean getCheckForActionContentType() {
        return checkForActionContentType
    }

    void setCheckForActionContentType(boolean checkForActionContentType) {
        this.checkForActionContentType = checkForActionContentType
    }

    boolean getCheckForQueryParameterExistence() {
        return checkForQueryParameterExistence
    }

    void setCheckForQueryParameterExistence(boolean checkForQueryParameterExistence) {
        this.checkForQueryParameterExistence = checkForQueryParameterExistence
    }

    boolean getCheckForPluralisedResourceNames() {
        return checkForPluralisedResourceNames
    }

    void setCheckForPluralisedResourceNames(boolean checkForPluralisedResourceNames) {
        this.checkForPluralisedResourceNames = checkForPluralisedResourceNames
    }

    boolean getCheckForSpecialCharactersInResourceNames() {
        return checkForSpecialCharactersInResourceNames
    }

    void setCheckForSpecialCharactersInResourceNames(boolean checkForSpecialCharactersInResourceNames) {
        this.checkForSpecialCharactersInResourceNames = checkForSpecialCharactersInResourceNames
    }

    boolean getCheckForDefinitionOf40xResponseInSecuredResource() {
        return checkForDefinitionOf40xResponseInSecuredResource
    }

    void setCheckForDefinitionOf40xResponseInSecuredResource(boolean checkForDefinitionOf40xResponseInSecuredResource) {
        this.checkForDefinitionOf40xResponseInSecuredResource = checkForDefinitionOf40xResponseInSecuredResource
    }

    String getCheckForSchemaInSuccessfulResponseBody() {
        return checkForSchemaInSuccessfulResponseBody
    }

    void setCheckForSchemaInSuccessfulResponseBody(String checkForSchemaInSuccessfulResponseBody) {
        this.checkForSchemaInSuccessfulResponseBody = checkForSchemaInSuccessfulResponseBody
    }

    boolean getCheckForDefinitionOfErrorCodes() {
        return checkForDefinitionOfErrorCodes
    }

    void setCheckForDefinitionOfErrorCodes(boolean checkForDefinitionOfErrorCodes) {
        this.checkForDefinitionOfErrorCodes = checkForDefinitionOfErrorCodes
    }

    String getCheckForSchemaInRequestBody() {
        return checkForSchemaInRequestBody
    }

    void setCheckForSchemaInRequestBody(String checkForSchemaInRequestBody) {
        this.checkForSchemaInRequestBody = checkForSchemaInRequestBody
    }

    boolean getCheckForDefinitionOf404ResponseInGetRequest() {
        return checkForDefinitionOf404ResponseInGetRequest
    }

    void setCheckForDefinitionOf404ResponseInGetRequest(boolean checkForDefinitionOf404ResponseInGetRequest) {
        this.checkForDefinitionOf404ResponseInGetRequest = checkForDefinitionOf404ResponseInGetRequest
    }

    boolean getCheckForResponseBodySchema() {
        return checkForResponseBodySchema
    }

    void setCheckForResponseBodySchema(boolean checkForResponseBodySchema) {
        this.checkForResponseBodySchema = checkForResponseBodySchema
    }

    boolean getBreakBuildOnWarnings() {
        return breakBuildOnWarnings
    }

    void setBreakBuildOnWarnings(boolean breakBuildOnWarnings) {
        this.breakBuildOnWarnings = breakBuildOnWarnings
    }

    boolean getLogWarnings() {
        return logWarnings
    }

    void setLogWarnings(boolean logWarnings) {
        this.logWarnings = logWarnings
    }

    boolean getLogErrors() {
        return logErrors
    }

    void setLogErrors(boolean logErrors) {
        this.logErrors = logErrors
    }

}
