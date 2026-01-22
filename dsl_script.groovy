def serviceName   = SERVICE_NAME
def repoUrl       = REPO_URL ?: GIT_URL               // allow either name
def repoDir       = REPO_DIR ?: SERVICE_NAME          // directory after clone (optional)
def gitCredId     = GIT_CRED_ID ?: 'gitlab-cred'
def dockerCredId  = DOCKER_CRED_ID ?: 'dockerhub-cred'
def dockerRepo    = DOCKER_REPO ?: 'jhinga'
def imageName     = IMAGE_NAME ?: SERVICE_NAME

folder('service') { displayName('Microservice') }

pipelineJob("services/${serviceName}") {
  description("Templated CI/CD for ${serviceName}\nRepo: ${repoUrl}")

  parameters {
    stringParam('REPO_URL', repoUrl, 'Git repo HTTPS URL')
    stringParam('BRANCH', 'main', 'Branch to build')
    stringParam('REPO_DIR', repoDir, 'Directory name after clone (optional)')
    stringParam('PROJECT_NAME', serviceName, 'Sonar project name')
    stringParam('PROJECT_KEY', serviceName, 'Sonar project key')
    stringParam('IMAGE_NAME', imageName, 'Docker image name (no registry/user)')
    stringParam('DOCKER_REPO', dockerRepo, 'DockerHub org/user (e.g. jhinga)')
  }

  definition {
    cps {
      script("""
pipeline {
  agent any

  stages {
    stage('Clean Workspace') {
      steps { cleanWs() }
    }

    stage('Clone Repo') {
      steps {
        withCredentials([usernamePassword(credentialsId: '${gitCredId}',
          usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          sh '''
            set -eux
            rm -rf "\${REPO_DIR}" || true
            git clone --branch "\${BRANCH}" "https://\${USERNAME}:\${PASSWORD}@\${REPO_URL#https://}" "\${REPO_DIR}"
          '''
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        dir("\${REPO_DIR}") {
          sh '''
            set -eux
            docker build -t "\${IMAGE_NAME}" .
          '''
        }
      }
    }

    stage('Push Docker Image to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: '${dockerCredId}',
          usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          sh '''
            set -eux
            echo "\$PASSWORD" | docker login -u "\$USERNAME" --password-stdin
            docker tag "\${IMAGE_NAME}" "\${DOCKER_REPO}/\${IMAGE_NAME}:\${BUILD_ID}"
            docker push "\${DOCKER_REPO}/\${IMAGE_NAME}:\${BUILD_ID}"
          '''
        }
      }
    }
  }

  post {
    always { cleanWs() }
  }
}
""".stripIndent())
      sandbox(true)
    }
  }
}
