pipeline {
    agent any

    environment {
        FRONTEND_IMAGE = 'gowthamhegde40/frontend:latest'
        BACKEND_IMAGE = 'gowthamhegde40/backend:latest'
        VM1_IP = 'your-vm1-private-ip'
    }

    stages {

        stage('Pull Code from Git') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-username/crud-dd-task-mean-app.git'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t ${FRONTEND_IMAGE} ./frontend'
                sh 'docker build -t ${BACKEND_IMAGE} ./backend'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push ${FRONTEND_IMAGE}'
                    sh 'docker push ${BACKEND_IMAGE}'
                }
            }
        }

        stage('Deploy to VM1') {
            steps {
                sshagent(['vm1-ssh-creds']) {
                    sh 'ssh -o StrictHostKeyChecking=no ubuntu@${VM1_IP} "cd /home/ubuntu/crud-dd-task-mean-app && docker compose pull && docker compose down && docker compose up -d"'
                }
            }
        }

    }

    post {
        success {
            echo 'Deployment Successful!'
        }
        failure {
            echo 'Deployment Failed!'
        }
    }
}