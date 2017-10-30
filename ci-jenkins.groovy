node {
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
}
