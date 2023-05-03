pipeline {
  agent any
  stages {
    stage("set variable") {
      steps {
        script {
          SLACK_CHANNEL = "#프로젝트"
          SLACK_SUCCESS_COLOR = "#2C9030";
          SLACK_FAIL_COLOR = "#FF3030";
          GIT_COMMIT_AUTHOR = sh(script: "git --no-pager show -s --format=%an ${env.GIT_COMMIT}", returnStdout: true).trim();
          GIT_COMMIT_MESSAGE = sh(script: "git --no-pager show -s --format=%B ${env.GIT_COMMIT}", returnStdout: true).trim();
        }
      }
      post {
        success {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_SUCCESS_COLOR,
            message: "파이프라인 시작\n${env.JOB_NAME}(${env.BUILD_NUMBER})\n${GIT_COMMIT_AUTHOR} - ${GIT_COMMIT_MESSAGE}\n
          )
        }
      }
    }

    stage('build and test') {
      steps {
        sh './gradlew clean build'
      }
      post {
        success {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_SUCCESS_COLOR,
            message: "Build 성공"
          )
        }
        failure {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_FAIL_COLOR,
            message: "Build 실패"
          )
        }
      }
    }

    stage('zip') {
      when {
        branch 'main'
      }
      steps {
        sh 'mv ./build/libs/woowachat.jar .'
        sh 'zip -r tecochat.zip .platform tecochat.jar Procfile'
      }
      post {
        success {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_SUCCESS_COLOR,
            message: "압축 성공"
          )
        }
        failure {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_FAIL_COLOR,
            message: "압축 실패"
          )
        }
      }
    }

    stage('upload') {
      when {
        branch 'main'
      }
      steps {
        sh 'aws s3 cp tecochat.zip s3://woowa-chat/tecochat.zip --region ap-northeast-2'
      }
      post {
        success {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_SUCCESS_COLOR,
            message: "S3 업로드 성공"
          )
        }
        failure {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_FAIL_COLOR,
            message: "S3 업로드 실패"
          )
        }
      }
    }

    stage('deploy') {
      when {
        branch 'main'
      }
      steps {
        sh 'aws elasticbeanstalk create-application-version --region ap-northeast-2 --application-name woowachat --version-label ${BUILD_TAG} --source-bundle S3Bucket="woowa-chat",S3Key="tecochat.zip"'
        sh 'aws elasticbeanstalk update-environment --region ap-northeast-2 --environment-name Woowachat-env --version-label ${BUILD_TAG}'
      }
      post {
        success {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_SUCCESS_COLOR,
            message: "Beanstalk 배포 성공"
          )
        }
        failure {
          slackSend (
            channel: SLACK_CHANNEL,
            color: SLACK_FAIL_COLOR,
            message: "Beanstalk 배포 실패"
          )
        }
      }
    }

  }
}
