#!/usr/bin/env groovy

node {
  def CONTAINER_BASE = null
  def FROM_IMAGE = null
  def INTERNAL_IMAGE_NAME = null
  def PUBLIC_IMAGE_NAME = null

  CONTAINER_BASE = "${GITLAB_INNERSOURCE_REGISTRY}/devops/images"
  FROM_IMAGE = "${CONTAINER_BASE}/${params.FROM_IMAGE}"

  PUBLIC_IMAGE_NAME = "usgs/${params.FROM_IMAGE}"
  INTERNAL_IMAGE_NAME = "${CONTAINER_BASE}/${PUBLIC_IMAGE_NAME}"

  try {
    stage('Initialize') {
      cleanWs()

      checkout scm

      if (params.GIT_BRANCH != '') {
        sh "git checkout --detach ${params.GIT_BRANCH}"
      }
    }

    stage('Build') {
      ansiColor('xterm') {
        sh """
          docker build \
            --build-arg FROM_IMAGE=${FROM_IMAGE} \
            -t ${INTERNAL_IMAGE_NAME} .
        """

        sh """
          docker tag \
            ${INTERNAL_IMAGE_NAME} \
            ${PUBLIC_IMAGE_NAME}
        """
      }
    }

    stage('Scan') {
      echo "TODO :: Implement scanning ..."
      // https://cisofy.com/lynis/
      // OR
      // https://www.open-scap.org/resources/documentation/perform-vulnerability-scan-of-rhel-6-machine/
      // (specific to RHEL6, but works for 7 with minor adjustments)
      // OR
      // ... ??? ...

      // Requirements ...
      // - Must be open source
      // - Must be free
      // - Must stay up-to-date with current CVE etc...
      // - Must be able to discern success/failure status
      //
      // - Should produce artifact report
    }

    stage('Publish') {
      docker.withRegistry(
        "https://${GITLAB_INNERSOURCE_REGISTRY}",
        'innersource-hazdev-cicd'
      ) {
        ansiColor('xterm') {
          sh "docker push ${INTERNAL_IMAGE_NAME}"
        }
      }


      docker.withRegistry('', 'usgs-docker-hub-credentials') {
        ansiColor('xterm') {
          sh "docker push ${PUBLIC_IMAGE_NAME}"
        }
      }
    }
  } catch (err) {
    try {
      mail([
        to: 'gs-haz_dev_team_group@usgs.gov',
        from: 'noreply@jenkins',
        subject: "Jenkins Pipeline Failed: ${env.BUILD_TAG}",
        body: "Details: ${err}"
      ])
    } catch (inner) {
      echo "An error occured while sending email. '${inner}'"
    }

    currentBuild.result = 'FAILURE'
    throw err
  }
}
