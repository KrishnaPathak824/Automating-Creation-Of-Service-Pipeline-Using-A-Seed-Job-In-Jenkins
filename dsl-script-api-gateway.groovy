def serviceName   = SERVICE_NAME
def repoFullName  = REPO_FULL_NAME
def repoUrl       = REPO_URL
def imageName     = IMAGE_NAME        // e.g. api_corporate
def folderName    = FOLDER_NAME       // e.g. APICORPORATE

folder('services-apigateway') { displayName('API Gateway Services') }

pipelineJob("services-apigateway/${serviceName}") {
  description("""API Gateway CI/CD for ${serviceName}
Repo: ${repoUrl}
Path: ${repoFullName}
""")

  definition {
    cps {
      script("""
pipeline {
  agent any

  stages {
    stage('Git Clone') {
      steps {
        git branch: 'testing1.1',
            url: '${repoUrl}',
            credentialsId: 'sblgitlab'
      }
    }

    stage('Build') {
      steps {
        sh '''
set -eux

# Paths
APP_ROOT="/var/jenkins_home/UAT_FAST/${folderName}"
COMMON_SRC="/var/jenkins_home/UAT_FAST/COMMON/backend/common"
COMMON_DST="\${APP_ROOT}/backend/common"

SVC_DIR="\${APP_ROOT}/backend/api-gateway/${serviceName}"
ENV_SRC="/var/jenkins_home/UAT_FAST/FILEENV/${serviceName}/.env.uat"
ENV_DST="\${SVC_DIR}/.env.uat"

# Sync common folder
rm -rf "\${COMMON_DST}"
cp -r "\${COMMON_SRC}" "\${APP_ROOT}/backend/"

# Update repo in the service dir (your original steps)
cd "\${SVC_DIR}"
git status && git config credential.helper store
git stash || true
git fetch --all
git checkout testing1.1
git pull origin testing1.1

# Copy env
cp -r "\${ENV_SRC}" "\${ENV_DST}"

# Dockerfile edits
sed -i 's/\\\${env}/.env.uat/g' Dockerfile
sed -i 's/node:18-alpine/node:22-alpine/g' Dockerfile

# Build with compose
cd "\${APP_ROOT}" && docker-compose build

# Harbor push
export BUILD_NUMBER="UAT.\${BUILD_NUMBER}"
cat /var/jenkins_home/secrets/passwd.txt | docker login https://harbor.swifttech.com.np --username admin --password-stdin

docker tag ${imageName}:latest harbor.swifttech.com.np/uat/${imageName}:\${BUILD_NUMBER}
docker push harbor.swifttech.com.np/uat/${imageName}:\${BUILD_NUMBER}
'''
      }
    }

    stage('Deploy') {
      steps {
        sshPublisher(
          continueOnError: false,
          failOnError: true,
          publishers: [
            sshPublisherDesc(
              configName: "k8s-uat",
              transfers: [
                sshTransfer(
                  sourceFiles: "target/*.zip",
                  removePrefix: "target",
                  remoteDirectory: "/root",
                  verbose: true,
                  execCommand: '''
set -eux

cd /root/kubernetes/application/APIGATEWAY
sudo cp /root/kubernetes/application/API/${imageName}.yml /root/kubernetes/application/APIGATEWAY/
sudo sed -i 's/latest/UAT.${BUILD_NUMBER}/g' ${imageName}.yml
kubectl apply -f ${imageName}.yml -n uat-gibl
kubectl rollout restart deployment ${serviceName}-service -n uat-gibl
sudo rm -rf ${imageName}.yml
'''
                )
              ]
            )
          ]
        )
      }
    }
  }
}
""".stripIndent())
      sandbox(true)
    }
  }
}
