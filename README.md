[![Build Status](https://travis-ci.org/netfalo/gradle-raml-plugin.svg?branch=master)](https://travis-ci.org/netfalo/gradle-raml-plugin)

Gradle Plugin based on the phoenixnap/springmvc-raml-plugin:  
https://github.com/phoenixnap/springmvc-raml-plugin

The plugin is delivered to the gradle plugin repository
https://plugins.gradle.org/plugin/com.netfalo.raml


## Add plugin dependecy
In the build.gradle add the plugin dependecy
```
plugins {
  id "com.netfalo.raml" version "0.1.0-beta1"
}
```

## Tasks
##### verifyRaml: 
start a verification from a already existing raml file

``` gradle verifyRaml ```

##### generateRaml:
generates a raml file from the current code

``` gradle generateRaml ```


# Configuration

### raml
```
raml {
   ...
}
```
#### defaultMediaType
Default value: application/json

#### dependencyPackagesList
Default value: [] (empty list)

#### ignoredList
Default value: [] (empty list)

#### javaDocPath
Default value: "" (empty string)


### Verify
```
raml {
    verify {
        ...
    }
}
```

#### ramlFile
The ramlFile option is mandatory, this should point to an already existing raml file
```
raml {
    verify {
        ramlFile = <full path to file>
    }
}
```
#### checkRamlAgainstImplementation
Default value: true

#### uriPrefixToIgnore
Default value: "" (empty string)

#### performStyleChecks
Default value: true

#### checkForResourceExistence
Default value: true

#### checkForActionExistence
Default value: true

#### checkForActionContentType
Default value:true

#### checkForQueryParameterExistence
Default value: true

#### checkForPluralisedResourceNames
Default value: true

#### checkForSpecialCharactersInResourceNames
Default value: true

#### checkForDefinitionOf40xResponseInSecuredResource
Default value: true

#### checkForSchemaInSuccessfulResponseBody
Default value: true

#### checkForDefinitionOfErrorCodes
Default value: false

#### checkForSchemaInRequestBody
Default value: "" (empty string)

#### checkForDefinitionOf404ResponseInGetRequest
Default value: false

#### checkForResponseBodySchema
Default value: false

#### breakBuildOnWarnings
Default value: false

#### logWarnings
Default value: true

#### logErrors
Default value: true


### Generate
```
raml {
    generate {
        ...
    }
}
```
#### outputRamlFilePath
Default filename is "api.raml"

#### restBasePath
Default value: "" (empty string)

#### restrictOnMediaType
Default value: false

#### includeGlobalMediaType
Default value: false

#### createPathIfMissing
Default value: false

#### removeOldOutput
Default value: false

#### version
Default value: "" (empty string)


# Developer info
After cloning: ```gradle publishToMavenLocal```

In you spring project's build.gradle add this to the beginning:
```
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.netfalo', name: 'gradle-raml-plugin',
                  version: '0.1.0-beta1'
    }
}
apply plugin: 'com.netfalo.raml'
```

At the moment only the raml generate and verify is implemented.
