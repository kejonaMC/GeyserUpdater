pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        }
        stage('Post') {
                    steps {
                         success {
                               archiveArtifacts 'target/GeyserUpdater-1.0-SNAPSHOT.jar'
                                 }

                          }
                       }
            }
}
