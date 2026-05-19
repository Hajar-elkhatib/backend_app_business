pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Code récupéré depuis GitHub ✓'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=backend-app \
                        -Dsonar.projectName=backend-app
                    '''
                }
            }
        }

        stage('Build Spring Boot') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t backend-app .'
            }
        }

    }

    post {
        success {
            echo '✅ Pipeline réussi !'
        }
        failure {
            echo '❌ Pipeline échoué — vérifier les logs'
        }
        always {
            cleanWs()
        }
    }
}
