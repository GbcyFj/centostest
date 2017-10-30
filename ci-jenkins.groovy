node {
  stage('update') {
    git changelog: false, poll: false, url: GIT_URL
  }

  stage('build') {
    sh '''
      docker build -t ${REGISTRY_HOST}/${IMAGE_NAME}:${IMAGE_VERSION} .
      docker image ls
    '''
  }

  stage('publish') {
    withCredentials([string(credentialsId: 'Jenkins CICD Containers (emartinez)', variable: 'REGISTRY_PASSWORD')]) {
      sh '''
        docker login "${REGISTRY_HOST}" -u "${REGISTRY_USERNAME}" -p "${REGISTRY_PASSWORD}"
      '''
    }
  }

  stage('cleanup') {
    sh '''
      docker image rm ${REGISTRY_HOST}/${IMAGE_NAME}:${IMAGE_VERSION}
    '''
  }
}
