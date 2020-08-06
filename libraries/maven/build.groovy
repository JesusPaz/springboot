void call() {
    stage('Build') {
        withMaven(maven: 'maven', jdk: 'jdk') {
            sh(' mvn clean package -DskipTests -U')
        }
    }
}
