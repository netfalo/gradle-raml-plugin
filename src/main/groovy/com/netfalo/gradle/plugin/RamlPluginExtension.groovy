package com.netfalo.gradle.plugin

class RamlPluginExtension {
    String defaultMediaType = "application/json"
    List<String> dependencyPackagesList = new ArrayList<String>()
    List<String> ignoredList = new ArrayList<String>()
    String javaDocPath = new String()
}

class GenerateRamlExtension {
    String outputRamlFilePath = "api.raml"
    String restBasePath = new String()
    Boolean restrictOnMediaType = false
    Boolean includeGlobalMediaType = false
    Boolean createPathIfMissing = false
    Boolean removeOldOutput = false
    String version = new String()
}

class VerifyRamlExtension {

    String ramlFile = new String()
    boolean checkRamlAgainstImplementation = true
    String uriPrefixToIgnore = new String()
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
    String checkForSchemaInRequestBody = new String()
    boolean checkForDefinitionOf404ResponseInGetRequest = false
    boolean checkForResponseBodySchema = false
    boolean breakBuildOnWarnings = false
    boolean logWarnings = true
    boolean logErrors = true
}
