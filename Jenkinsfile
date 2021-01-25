pipeline {
    agent any
    tools {
       maven 'Maven 3'
       jdk 'Java 8'
       }

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        }
    }
}