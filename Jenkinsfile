pipeline {
    agent any

    environment {
        AWS_REGION = 'eu-north-1'
        CLUSTER_NAME = 'ekscluster'
        ECR_REPO = 'awsdockerimages'
        ECR_URI = "522448740060.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}"
        IMAGE_TAG = 'latest'
    }

    stages {
        stage("Checkout Source Code") {
            steps {
                git 'https://github.com/rajesh-305/springbootwebapp.git'
                slackSend channel: 'new-channel', message: 'Source code checked out successfully'
            }
        }
        stage("Compile Source") {
            steps {
                sh 'mvn compile'
                slackSend channel: 'new-channel', message: 'Compilation done successfully'
            }
        }
        stage("Run Test Cases") {
            steps {
                sh 'mvn test'
                slackSend channel: 'new-channel', message: 'Test cases ran successfully'
            }
        }
        stage("Create Package") {
            steps {
                sh 'mvn package'
                slackSend channel: 'new-channel', message: 'build artifact created successfully'
            }
        }
        stage("Create ECR Repository") {
            steps {
                withAWS(credentials: 'aws-credentials', region: "${AWS_REGION}") {
                    script {
                        def repoExists = sh(script: "aws ecr describe-repositories --repository-names ${ECR_REPO} --region ${AWS_REGION} --query 'repositories[0].repositoryName' --output text", returnStatus: true) == 0
                        if (!repoExists) {
                            sh "aws ecr create-repository --repository-name ${ECR_REPO} --region ${AWS_REGION}"
                            echo "ECR repository ${ECR_REPO} created successfully."
                            slackSend channel: 'new-channel', message: 'ECR successful'
                        } else {
                            echo "ECR repository ${ECR_REPO} already exists."
                            slackSend channel: 'new-channel', message: 'ECR creation failed'
                        }
                    }
                }
            }
        }
        stage("Build Docker Image") {
            steps {
                sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URI}"
                sh "docker build -t ${ECR_REPO} ."
                slackSend channel: 'new-channel', message: 'Built successfully '
            }
        }
        stage("Tag Docker Image") {
            steps {
                sh "docker tag ${ECR_REPO}:latest ${ECR_URI}:${IMAGE_TAG}"
                slackSend channel: 'new-channel', message: 'Success!'
            }
        }
        stage("Push Docker Image to ECR") {
            steps {
                sh "docker push ${ECR_URI}:${IMAGE_TAG}"
                slackSend channel: 'new-channel', message: 'Success!'
            }
        }
        stage("Setup AWS Credentials") {
            steps {
                withCredentials([[ 
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    sh 'export AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID'
                    sh 'export AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY'
                    sh 'aws sts get-caller-identity'
                }
            }
        }
        /*
        stage("Create EKS Cluster") {
            steps {
                sh """
                eksctl create cluster \\
                    --name ${CLUSTER_NAME} \\
                    --region ${AWS_REGION} \\
                    --nodes 3 \\
                    --node-type t3.medium
                """
            }
        }
        stage("Update Kubeconfig") {
            steps {
                sh "aws eks update-kubeconfig --name ${CLUSTER_NAME} --region ${AWS_REGION}"
                sh "kubectl get nodes"
            }
        } */
        stage("Continuous_Delivery")
        {
            steps
            {
                slackSend channel: 'new-channel', message: 'Waiting for approval from Shekar'
                input message: 'Waiting for approval from Shekar', submitter: 'shekar'
                
            }
        }
        stage("Deploy Pod to EKS") {
            steps {
                script {
                    def podManifest = """
apiVersion: v1
kind: Pod
metadata:
  name: springbootwebapp
spec:
  containers:
  - name: springbootwebapp
    image: ${ECR_URI}:${IMAGE_TAG}
    ports:
    - containerPort: 8080
"""
                    writeFile file: 'pod.yaml', text: podManifest
                    sh "kubectl apply -f pod.yaml"
                    sh "kubectl get pods"
                }
            }
        }
		 stage("Expose Pod via NodePort Service") {
            steps {
                script {
                    def serviceManifest = """
apiVersion: v1
kind: Service
metadata:
  name: springbootwebapp-service
spec:
  type: NodePort
  selector:
    name: springbootwebapp
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
      nodePort: 30080
"""
                    // Patch pod label for selector
                    sh "kubectl label pod springbootwebapp name=springbootwebapp --overwrite"
                    writeFile file: 'service.yaml', text: serviceManifest
                    sh "kubectl apply -f service.yaml"
                    sh "kubectl get svc"
                }
            }
        }

    }
    post {
        success {
slackSend channel: 'new-channel', message: 'The pipeline executed successfully'        
            
        }
        failure {
            slackSend channel: 'new-channel', message: 'Pipeline Failed'
        }
    }
}
