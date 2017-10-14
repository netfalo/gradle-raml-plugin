[![Build Status](https://travis-ci.org/netfalo/gradle-raml-plugin.svg?branch=master)](https://travis-ci.org/netfalo/gradle-raml-plugin)

Gradle Plugin based on the phoenixnap/springmvc-raml-plugin:  
https://github.com/phoenixnap/springmvc-raml-plugin

#Usage
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
                  version: '1.0-SNAPSHOT'
    }
}
apply plugin: 'com.netfalo.raml'
```

At the moment only the raml generate and verify is implemented.
