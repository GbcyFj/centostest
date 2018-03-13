#!/usr/bin/env groovy

node {
  def CONTAINER_BASE = null
  def FROM_IMAGE = null
  def IMAGE_NAME = null
  def IMAGE_VERSION = null
  def SCM_VARS = null

  CONTAINER_BASE = "${GITLAB_INNERSOURCE_REGISTRY}/devops/images"
  FROM_IMAGE = "${CONTAINER_BASE}/centos"
  IMAGE_NAME = "${CONTAINER_BASE}/usgs/centos"
  IMAGE_VERSION = params.FROM_IMAGE_TAG

  try {
    stage('Initialize') {
      cleanWs()

      SCM_VARS = checkout scm

      if (params.GIT_BRANCH != '') {
        sh "git checkout --detach ${params.GIT_BRANCH}"

        SCM_VARS.GIT_BRANCH = params.GIT_BRANCH
        SCM_VARS.GIT_COMMIT = sh(
          returnStdout: true,
          script: "git rev-parse HEAD"
        )
      }
    }

    stage('Build') {
      ansiColor('xterm') {
        sh """
          docker build \
            --build-arg FROM_IMAGE=${FROM_IMAGE}:${IMAGE_VERSION} \
            -t ${IMAGE_NAME}:${IMAGE_VERSION} .
        """

        sh """
          docker tag \
            ${IMAGE_NAME}:${IMAGE_VERSION} \
            usgs/centos:${IMAGE_VERSION}
        """
      }
    }

    stage('Publish') {
      docker.withRegistry(
        "https://${GITLAB_INNERSOURCE_REGISTRY}",
        'innersource-hazdev-cicd'
      ) {
        ansiColor('xterm') {
          sh "docker push ${IMAGE_NAME}:${IMAGE_VERSION}"
        }
      }

      withCredentials([usernamePassword(
        credentialsId: 'usgs-docker-hub-credentials',
        passwordVariable: 'HUB_USERNAME',
        usernameVariable: 'HUB_PASSWORD'
      )]) {

        docker.withRegistry('', 'usgs-docker-hub-credentials') {
          ansiColor('xterm') {
            sh "docker login -u ${HUB_USERNAME} -p ${HUB_PASSWORD}"
            sh "docker push usgs/centos:${IMAGE_VERSION}"
          }
        }
      }
    }
  } catch (err) {
    try {
      // mail([
      //   to: 'gs-haz_dev_team_group@usgs.gov',
      //   from: 'noreply@jenkins',
      //   subject: "Jenkins Pipeline Failed: ${env.BUILD_TAG}",
      //   body: "Details: ${err}"
      // ])
    } catch (inner) {
      echo "An error occured while sending email. '${inner}'"
    }

    currentBuild.result = 'FAILURE'
    throw err
  }
}
