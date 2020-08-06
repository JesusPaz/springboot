void call() {
    stage('Test') {
        withMaven(maven: 'maven', jdk: 'jdk') {
            sh('mvn clean test -U')
        }
    }
}
