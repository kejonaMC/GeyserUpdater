pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        }
        stage('Deploy') {
             when {
                expression {
                  currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
             }
             steps {
                  sh 'make publish'
             }
        }
    }
}