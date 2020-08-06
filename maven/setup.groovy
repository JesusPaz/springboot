def getVersion() {
    def version = sh(script:"mvn -q -Dexec.executable=echo -Dexec.args='\${project.version}' --non-recursive exec:exec | tail -1", returnStdout: true)
    return version.replace('\n', '')
}

void call() {
    stage('Setup') {
        checkout scm
        withMaven(maven: 'maven', jdk: 'jdk') {
            version = getVersion()
        }
        echo ("${version}")
    }
}
