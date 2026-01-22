#!/usr/bin/env groovy

/**
 * Jenkins Seed Job DSL Script
 * 
 * This seed job automatically creates Jenkins pipeline jobs for all microservices
 * defined in the services configuration file. It reads the configuration and generates
 * pipeline jobs with consistent structure across all services.
 * 
 * Usage:
 * 1. Configure services in config/services.yaml
 * 2. Run this seed job in Jenkins
 * 3. All service pipelines will be automatically created/updated
 */

import groovy.yaml.YamlSlurper

// Read the services configuration file
def servicesConfigFile = readFileFromWorkspace('config/services.yaml')
def config = new YamlSlurper().parseText(servicesConfigFile)

// Get common configuration
def organization = config.organization ?: 'MyOrganization'
def gitBaseUrl = config.gitBaseUrl ?: 'https://github.com'
def credentials = config.credentials ?: 'github-credentials'
def jenkinsfilePath = config.jenkinsfilePath ?: 'Jenkinsfile'

// Create a folder for all service pipelines
folder("${organization}-Services") {
    displayName("${organization} Microservices")
    description("Auto-generated folder containing all ${organization} microservice pipelines")
}

// Iterate through each service and create a pipeline job
config.services.each { service ->
    def serviceName = service.name
    def repoName = service.repo ?: serviceName
    def branch = service.branch ?: 'main'
    def description = service.description ?: "Pipeline for ${serviceName} microservice"
    def scheduleExpression = service.schedule ?: ''
    def parameters = service.parameters ?: []
    
    // Create pipeline job for the service
    pipelineJob("${organization}-Services/${serviceName}") {
        displayName(serviceName)
        description(description)
        
        // Add build parameters if defined
        parameters.each { param ->
            switch(param.type) {
                case 'string':
                    stringParam(param.name, param.defaultValue ?: '', param.description ?: '')
                    break
                case 'boolean':
                    booleanParam(param.name, param.defaultValue ?: false, param.description ?: '')
                    break
                case 'choice':
                    choiceParam(param.name, param.choices ?: [], param.description ?: '')
                    break
            }
        }
        
        // Configure the pipeline
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url("${gitBaseUrl}/${organization}/${repoName}.git")
                            credentials(credentials)
                        }
                        branches(branch)
                        extensions {
                            cleanBeforeCheckout()
                        }
                    }
                }
                scriptPath(jenkinsfilePath)
            }
        }
        
        // Add triggers if schedule is defined
        if (scheduleExpression) {
            triggers {
                cron(scheduleExpression)
            }
        }
        
        // Configure build options
        properties {
            buildDiscarder {
                strategy {
                    logRotator {
                        daysToKeepStr('30')
                        numToKeepStr('10')
                        artifactDaysToKeepStr('30')
                        artifactNumToKeepStr('5')
                    }
                }
            }
        }
        
        // Add additional configuration if specified
        if (service.enableConcurrentBuilds) {
            concurrentBuild(true)
        } else {
            concurrentBuild(false)
        }
    }
}

// Create a view to display all service pipelines
listView("${organization}-Services-View") {
    description("Overview of all ${organization} microservice pipelines")
    jobs {
        regex("${organization}-Services/.*")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

println "Successfully generated ${config.services.size()} service pipeline(s)"
