#!/usr/bin/env groovy

def buildDockerImage(version, snapshot) {
    def host = "629546332162.dkr.ecr.us-east-1.amazonaws.com"
    def repo = "springboot"
    def region = "us-east-1"
    def tag = "${version}-${snapshot}"
    def endpoint = "${host}/${repo}:${tag}"
    echo("Building docker image: ${endpoint}")
    sh("\$(aws ecr get-login --no-include-email  --region ${region})")
    sh("docker build . -t ${endpoint}")
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
    sh("cp target/demoBack*.jar docker/demoBack.jar")
}

def getVersion() {
    def version= sh(script:"mvn -q -Dexec.executable=echo -Dexec.args='\${project.version}' --non-recursive exec:exec | tail -1", returnStdout: true)
    return version.replace("\n", "")

}

def publishImage(image) {
    sh("aws ecr batch-delete-image --repository-name=${image.repo} --image-ids imageTag=${image.tag} --region us-east-1")
    echo("Pushing image: ${image.endpoint}")
    sh("docker push ${image.endpoint}")
    sh("docker rmi ${image.endpoint}")
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
        /*stage("Test") {
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
        }*/
        stage("Deploy") {
            withAWS(region:"us-east-1") {
              image = "pepe"
              //echo("Deploying demo version ${image.endpoint} into demo environment")
              writeFile file: "demoback/demoimagedef.json", text: "[{\"name\":\"demo\",\"imageUri\":\"${image}\"}]"
              sh("ls")
              zip dir: "demoback", glob: "", zipFile: "demo.zip"
              s3Upload acl: 'Private', bucket: "eafit-deploy", file: "demo.zip", path: "demo/", workingDir: ''
            }
        }
    } catch(error) {
        throw error
    } finally {
        cleanWs()
    }
}