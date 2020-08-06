def publishImage(image) {
    sh("aws ecr batch-delete-image --repository-name=${image.repo} --image-ids imageTag=${image.tag} --region us-east-2")
    echo("Pushing image: ${image.endpoint}")
    sh("sudo docker push ${image.endpoint}")
    sh("sudo docker rmi ${image.endpoint}")
}

void call() {
    node() {
        stage('Publish Image') {
            publishImage(image)
        }
    }
}
