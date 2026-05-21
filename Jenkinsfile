pipeline {
    agent any

    environment {
        IMAGE = "marouamrouji/backend-app"
        TAG = "1.0.${env.BUILD_NUMBER}"
    }

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
                    sh 'mvn sonar:sonar -Dsonar.projectKey=backend-app -Dsonar.projectName=backend-app'
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
                sh 'mvn test || true'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE}:${TAG} ."
                sh "docker tag ${IMAGE}:${TAG} ${IMAGE}:latest"
            }
        }

        stage('Push Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS')]) {
                    sh "echo $PASS | docker login -u $USER --password-stdin"
                    sh "docker push ${IMAGE}:${TAG}"
                    sh "docker push ${IMAGE}:latest"
                }
            }
        }

    }

    post {
        success {
            echo '✅ Pipeline backend réussi !'
        }
        failure {
            echo '❌ Pipeline backend échoué — vérifier les logs'
        }
        always {
            cleanWs()
        }
    }
}
