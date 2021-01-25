pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        stage('Post') {
                 post {
                     success {
                         archiveArtifacts 'target/GeyserUpdater-1.0-SNAPSHOT.jar'
                     }
                 }
        }
    }
}