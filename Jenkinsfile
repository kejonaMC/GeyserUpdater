properties([pipelineTriggers([githubPush()])])

pipeline {
    agent any

    stages {
         /* checkout repo */
         stage('Checkout SCM') {
             steps {
                 checkout([
                  $class: 'GitSCM',
                  branches: [[name: 'main']],
                  userRemoteConfigs: [[
                     url: 'git@github.com:YHDiamond/GeyserUpdater.git',
                     credentialsId: '',
                  ]]
                 ])
             }
         }
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        }
        stage('Post') {
            steps {
                archiveArtifacts 'target/GeyserUpdater-1.0-SNAPSHOT.jar'
                  }

                }

        }
}
