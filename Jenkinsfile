#!/usr/bin/env groovy

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

def copyJar() {
    sh('cp target/demoBack*.jar docker/demoBack.jar')
}

def getVersion() {
    def version = sh(script:"mvn -q -Dexec.executable=echo -Dexec.args='\${project.version}' --non-recursive exec:exec | tail -1", returnStdout: true)
    return version.replace('\n', '')
}

def publishImage(image) {
    sh("aws ecr batch-delete-image --repository-name=${image.repo} --image-ids imageTag=${image.tag} --region us-east-2")
    echo("Pushing image: ${image.endpoint}")
    sh("sudo docker push ${image.endpoint}")
    sh("sudo docker rmi ${image.endpoint}")
}

node() {
    def version, image
    try{
        stage("Setup"){
            checkout scm
            withMaven(maven: 'maven', jdk: 'jdk') {
                version = getVersion()
            }
            echo ("${version}")
        }
        stage("Test") {
            withMaven(maven: 'maven', jdk: 'jdk') {
                sh("mvn clean test -U")
            }
        }
        stage("Build") {
            withMaven(maven: 'maven', jdk: 'jdk') {
                sh(" mvn clean package -DskipTests -U")
            }
        }
        stage("Build Image") {
            copyJar()
            dir("docker") {
                image = buildDockerImage(version, env.BRANCH_NAME)
            }
        }
        stage("Publish Image") {
            publishImage(image)
        }
    } catch(error) {
        throw error
    } finally {
        cleanWs()
    }
}