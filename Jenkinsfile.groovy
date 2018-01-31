#!/usr/bin/env groovy

node {
  def CONTAINER_BASE = null
  def FROM_IMAGE = null
  def IMAGE_NAME = null
  def SCM_VARS = null

  IMAGE_VERSION = 'latest'
  CONTAINER_BASE = "${GITLAB_INNERSOURCE_REGISTRY}/devops/containers"
  FROM_IMAGE = "${CONTAINER_BASE}/library/centos"
  IMAGE_NAME = "${CONTAINER_BASE}/centos"

  try {
    stage('Initialize') {
      cleanWs()

      SCM_VARS = checkout scm

      SCM_VARS.each { key, value ->
        echo "${key} = ${value}"
      }
    }

    // stage('Build') {
    //   ansiColor('xterm') {
    //     sh """
    //       docker build \
    //         --build-arg FROM_IMAGE=${FROM_IMAGE}:${params.IMAGE_VERSION}
    //         -t ${IMAGE_NAME}:${params.IMAGE_VERSION} .
    //     """
    //   }
    // }

    // stage('Publish') {
    //   withCredentials([usernamePassword(
    //       credentialsId: 'innersource-hazdev-cicd',
    //       passwordVariable: 'REGISTRY_PASSWORD',
    //       usernameVariable: 'REGISTRY_USERNAME')
    //   ]) {
    //     ansiColor('xterm') {
    //       sh """
    //         docker login ${GITLAB_INNERSOURCE_REGISTRY} \
    //           -u ${REGISTRY_USERNAME} \
    //           -p ${REGISTRY_PASSWORD}

    //         docker push ${IMAGE_NAME}:${params.IMAGE_VERSION}
    //       """
    //     }

    //     if (params.TAG_LATEST && params.IMAGE_VERSION != 'latest') {
    //       ansiColor('xterm') {
    //         sh """
    //           docker tag \
    //             ${IMAGE_NAME}:${params.IMAGE_VERSION} \
    //             ${IMAGE_NAME}:latest

    //           docker login ${GITLAB_INNERSOURCE_REGISTRY} \
    //             -u ${REGISTRY_USERNAME} \
    //             -p ${REGISTRY_PASSWORD}

    //           docker push ${IMAGE_NAME}:${latest}
    //         """
    //       }
    //     }
    //   }
    // }
  } catch (err) {
    def message
    def printWriter
    def stringWriter

    stringWriter = new StringWriter()
    printWriter = new PrintWriter(stringWriter)
    err.printStackTrace(printWriter)

    message = stringWriter.toString()

    mail([
      to: 'gs-haz_dev_team_group@usgs.gov',
      from: 'noreply@jenkins',
      subject: "Jenkins Pipeline Failed: ${env.BUILD_TAG}",
      body: "Details: ${message}"
    ])

    currentBuild.result = 'FAILURE'
    throw err
  }
}
