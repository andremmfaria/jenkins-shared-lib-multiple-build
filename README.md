# jenkins-shared-lib-multiple-build

This is a Jenkins shared library definition to implement automated pipeline definitions to projects without explicitly defining the pipelines on the project's Jenkinsfile.
This uses the multibranch pipeline definitions by default.

At the moment there are only two types of projects with the pipeline in place.

* Docker
* Java (using gradle)

There are many configurations needed to be done in jenkins for this to work, like SonarQube and Nexus.

When everything is in place the Jenkinsfile on your project needs only to declare the use of the library, set the 'engine', 'type' and any other desired configs and call the 'execPipeline' command. As follows:

Jenkinsfile:
```
@Library('execPipeline') _

def config = [:]
config.'engine' = 'java'
config.'type' = 'app'

execPipeline(config)
```