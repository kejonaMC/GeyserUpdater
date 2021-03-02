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
                archiveArtifacts 'target/GeyserUpdater-1.2.0-SNAPSHOT.jar'
                  }

                }
        }
}
