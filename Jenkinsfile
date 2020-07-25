#!groovy

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
                sh 'mvn validate'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
                sh 'mvn verify'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
