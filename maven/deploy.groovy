void call() {
    node() {
        stage('Deploy') {
            sh('aws eks --region us-east-2 update-kubeconfig --name sf_rampup_eks_cluster')
            sh('helm install ./springboot-chart --generate-name')
        }
    }
}
