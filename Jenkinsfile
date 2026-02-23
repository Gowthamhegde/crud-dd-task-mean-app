pipeline {
    agent any
    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_USERNAME = 'gowthamhegde04'
        VM_HOST = credentials('vm-host')
        VM_USER = credentials('vm-username')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Backend Image') {
            steps {
                script {
                    dir('backend') {
                        sh "docker build -t ${DOCKER_USERNAME}/backend:latest ."
                    }
                }
            }
        }
        
        stage('Build Frontend Image') {
            steps {
                script {
                    dir('frontend') {
                        sh "docker build -t ${DOCKER_USERNAME}/frontend:latest ."
                    }
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
                    sh "docker push ${DOCKER_USERNAME}/backend:latest"
                    sh "docker push ${DOCKER_USERNAME}/frontend:latest"
                }
            }
        }
        
        stage('Deploy to VM') {
            steps {
                script {
                    sshagent(['vm-ssh-key']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${VM_USER}@${VM_HOST} '
                                cd /path/to/your/app &&
                                docker-compose pull &&
                                docker-compose down &&
                                docker-compose up -d &&
                                docker image prune -f
                            '
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh 'docker logout'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
