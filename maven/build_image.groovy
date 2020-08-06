def copyJar() {
    sh('cp target/demoBack*.jar docker/demoBack.jar')
}

def buildDockerImage(version, snapshot) {
    def host = '961518039473.dkr.ecr.us-east-2.amazonaws.com'
    def repo = 'sf-rampup'
    def region = 'us-east-2'
    def tag = "${version}-${snapshot}"
    def endpoint = "${host}/${repo}:${tag}"
    echo("Building docker image: ${endpoint}")
    sh("aws ecr get-login-password --region ${region} | sudo docker login --username AWS --password-stdin ${host}")
    sh("sudo docker build . -t ${endpoint}")

    def image =  [
        version: "${version}",
        snapshot:"${snapshot}",
        repo: "${repo}",
        host:"${host}",
        endpoint:"${endpoint}",
        tag:"${tag}"
    ]
    return image
}

void call() {
    node() {
        stage('Build Image') {
            copyJar()
            dir('docker') {
                image = buildDockerImage(version, env.BRANCH_NAME)
            }
        }
    }
}
