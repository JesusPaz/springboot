void call() {
    node() {
        stage('Test') {
            withMaven(maven: 'maven', jdk: 'jdk') {
                sh('mvn clean test -U')
            }
        }
    }
}
