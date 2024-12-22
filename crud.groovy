pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-2'  // Update with your AWS region
        ECR_URI = '062927239149.dkr.ecr.us-east-2.amazonaws.com/yash/crud'  // Your specific ECR URI
    }

    stages {
        // Stage 1: Checkout Code from GitHub
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/yashsinghshiva/yash_crud_application.git', branch: 'main'
            }
        }

        // Stage 2: Authenticate Docker to AWS ECR
        stage('Authenticate Docker to AWS ECR') {
            steps {
                script {
                    // Authenticate with ECR using the AWS CLI
                    sh 'aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_URI'
                }
            }
        }

        // Stage 3: Build Docker Image
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image
                    sh 'docker build -t yash_crud .'
                }
            }
        }

        // Stage 4: Tag Docker Image for ECR
        stage('Tag Docker Image') {
            steps {
                script {
                    // Tag the image with the ECR repository URI
                    sh 'docker tag yash_crud $ECR_URI:latest'
                }
            }
        }

        // Stage 5: Push Docker Image to AWS ECR
        stage('Push Docker Image') {
            steps {
                script {
                    // Push the image to Amazon ECR
                    sh 'docker push $ECR_URI:latest'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully. Docker image pushed to AWS ECR.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}
