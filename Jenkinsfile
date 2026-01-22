import jenkins.model.Jenkins

pipeline {
  agent any

  environment {
    GITLAB_URL = 'https://gitlab.swifttech.com.np'
    GROUP_ID   = '358'
  }

  stages {
    stage('Poll GitLab group projects') {
      steps {
        withCredentials([string(credentialsId: 'gitlab-cred', variable: 'GITLAB_TOKEN')]) {
          script {
            def projects = []
            def page = 1

            while (true) {
              def apiUrl = "${env.GITLAB_URL}/api/v4/groups/${env.GROUP_ID}/projects?per_page=100&page=${page}"

              def resp = sh(
                script: """#!/bin/bash
set -euo pipefail
curl -sS --header "PRIVATE-TOKEN: \$GITLAB_TOKEN" '${apiUrl}'
""",
                returnStdout: true
              ).trim()

              def arr = readJSON text: resp
              if (!arr || arr.size() == 0) break

              projects.addAll(arr)
              page++
            }

            echo "Found ${projects.size()} projects"
            // TODO: loop projects and call jobDsl(...) as before
              for (p in projects) {
                if (p.archived) {
                  echo "Archived: ${p.path_with_namespace} (skip)"
                  continue
                }
              
                def serviceName   = p.path
                def repoFullName  = p.path_with_namespace
                def gitSshUrl     = p.ssh_url_to_repo
                def gitHttpUrl    = p.http_url_to_repo
                def defaultBranch = (p.default_branch ?: "main") as String
              
                echo "Ensuring job exists: services/${serviceName}"
              
                jobDsl(
                  targets: 'dsl/create_service_pipeline.groovy',
                  removedJobAction: 'IGNORE',
                  additionalParameters: [
                    SERVICE_NAME   : serviceName,
                    REPO_FULL_NAME : repoFullName,
                    GIT_URL        : gitSshUrl,
                    REPO_URL       : gitHttpUrl,
                    REPO_DIR       : serviceName,
                    DEFAULT_BRANCH : defaultBranch,
                    DOCKER_REPO    : 'jhinga',
                    IMAGE_NAME     : serviceName,
                    DOCKER_CRED_ID  : 'dockerhub-cred',
                    GIT_CRED_ID    : 'gitlab-cred'
                  ]
                )
              }


          }
        }
      }
    }
  }
}
