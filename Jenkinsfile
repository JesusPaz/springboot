#!/usr/bin/env groovy
import groovy.json.JsonSlurperClassic
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
        // stage("Test") {
        //     withMaven(maven: 'maven', jdk: 'jdk') {
        //         sh("mvn clean test -U")
        //     }
        // }
        // stage("Build") {
        //     withMaven(maven: 'maven', jdk: 'jdk') {
        //         sh(" mvn clean package -DskipTests -U")
        //     }
        // }
        // stage("Build Image") {
        //     copyJar()
        //     dir("docker") {
        //         image = buildDockerImage(version, env.BRANCH_NAME)
        //     }
        // }
        // stage("Publish Image") {
        //     publishImage(image)
        // }
        stage("Deploy") {
            sh("aws eks --region us-east-2 update-kubeconfig --name sf_rampup_eks_cluster")
            sh("helm install ./springboot-chart --generate-name")
        }
        // stage("Validate deploy finished") {
        //     sleep 5
        //     timeout(15) {
        //       waitUntil {
        //       withAWS(region:"us-east-1") {
        //             script {
        //               def pipelineStatusJson = sh(script: "aws codepipeline get-pipeline-state --name demo-deploy"
        //                   , returnStdout: true)
        //               def jsonSlurper = new JsonSlurperClassic()
        //               def pipelineStatus = jsonSlurper.parseText(pipelineStatusJson)
        //               def stages = pipelineStatus["stageStates"]
        //               def breakFlag = false
        //               def finished = false
        //               for(stage in stages) {
        //                 def status = stage["latestExecution"]["status"]
        //                 def name = stage["stageName"]
        //                 echo("Current status for stage : ${name} is ${status}")
        //                 if("Failed" == status ) {
        //                   error("Deploy failed")
        //                 }
        //                 if("InProgress" == status) {
        //                   breakFlag = true
        //                   break
        //                 }
        //               }
        //               echo("Break falg is : ${breakFlag}")
        //               if (!breakFlag) {
        //                 finished = true
        //               }
        //               return finished
        //             }
        //         }
        //       }
        //     }
        // }
    } catch(error) {
        throw error
    } finally {
        cleanWs()
    }
}