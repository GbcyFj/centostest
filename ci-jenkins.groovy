node {
  stage('build') {
    sh '''
      pwd
      ls
      docker --version
      docker container ls -a
      docker image ls
    '''
  }
}
