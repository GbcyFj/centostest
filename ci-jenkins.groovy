node {
  stage('update') {
    git changelog: false, poll: false, url: GIT_URL
  }

  stage('build') {
    sh '''
      pwd
      ls -la
      echo ${IMAGE_TAG_VERSION}
      docker --version
      docker container ls -a
      docker image ls
    '''
  }

  stage('publish') {

  }

  stage('cleanup') {

  }
}
