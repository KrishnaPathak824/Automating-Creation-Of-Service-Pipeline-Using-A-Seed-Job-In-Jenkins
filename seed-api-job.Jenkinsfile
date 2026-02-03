pipeline {
  agent any

  environment {
    GITLAB_URL = 'https://gitlab.swifttech.com.np'
    GROUP_ID   = '361'
  }

  stages {
    stage('Poll GitLab group projects') {
      steps {
        withCredentials([string(credentialsId: 'gitlab-cred', variable: 'GITLAB_TOKEN')]) {
          script {
            def projects = []
            def page = 1

            while (true) {
              def apiUrl = "${env.GITLAB_URL}/api/v4/groups/${env.GROUP_ID}/projects?include_subgroups=true&per_page=100&page=${page}"

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
                def gitHttpUrl    = p.http_url_to_repo
                def imageName     = serviceName.replace('-', '_')  // api_corporate
                def folderName    = serviceName.replace('-', '').toUpperCase() // APICORPORATE

  
                echo "imageName=${imageName}, folderName=${folderName}"
              
                echo "Ensuring job exists: services/${serviceName}"
              
                jobDsl( targets: 'dsl/create_service_pipeline.groovy', removedJobAction: 'IGNORE', additionalParameters: [ SERVICE_NAME : serviceName, REPO_FULL_NAME : repoFullName, REPO_URL : gitHttpUrl, IMAGE_NAME : imageName, FOLDER_NAME : folderName, ] )
              }
          }
        }
      }
    }
  }
}
