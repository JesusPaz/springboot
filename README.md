## Deploy ALB Ingress Controller

´´´
eksctl utils associate-iam-oidc-provider --cluster=sf_rampup_eks_cluster --approve

ALB_INGRESS_VERSION=v1.1.8

kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/aws-alb-ingress-controller/$ALB_INGRESS_VERSION/docs/examples/rbac-role.yaml

### create the policy
aws iam create-policy \
  --policy-name ALBIngressControllerIAMPolicy \
  --policy-document https://raw.githubusercontent.com/kubernetes-sigs/aws-alb-ingress-controller/$ALB_INGRESS_VERSION/docs/examples/iam-policy.json

### get the policy ARN
export PolicyARN=$(aws iam list-policies --query 'Policies[?PolicyName==`ALBIngressControllerIAMPolicy`].Arn' --output text)

eksctl create iamserviceaccount \
        --cluster=eksworkshop-eksctl \
        --namespace=kube-system \
        --name=alb-ingress-controller \
        --attach-policy-arn=$PolicyARN \
        --override-existing-serviceaccounts \
        --approve

### We dynamically replace the cluster-name by the name of our cluster before applying the YAML file
curl -sS "https://raw.githubusercontent.com/kubernetes-sigs/aws-alb-ingress-controller/${ALB_INGRESS_VERSION}/docs/examples/alb-ingress-controller.yaml" \
    | sed 's/# - --cluster-name=devCluster/- --cluster-name=eksworkshop-eksctl/g' \
    | kubectl apply -f -


kubectl logs -n kube-system $(kubectl get po -n kube-system | egrep -o alb-ingress[a-zA-Z0-9-]+)


´´´