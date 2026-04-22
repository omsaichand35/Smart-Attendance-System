pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        DOCKER_IMAGE = 'smart-attendance-backend'
        DOCKER_TAG = 'latest'
        PROJECT_DIR = '/Users/arunodayahosakeri/Documents/DevOps/Smart-Attendance-System'
    }

    stages {
        stage('Start Database for Tests') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo 'Spinning up MSSQL database so our unit tests can connect to it...'
                    sh 'docker-compose up -d db'
                    sleep 15
                }
            }
        }

        stage('Unit Tests') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo 'Running Maven tests inside Docker...'
                    sh '''
                    docker run --rm --network host \
                    -e SPRING_DATASOURCE_URL="jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=false;trustServerCertificate=true" \
                    -e SPRING_DATASOURCE_USERNAME=SA \
                    -e SPRING_DATASOURCE_PASSWORD=YourStrong@Passw0rd \
                    -v "${PROJECT_DIR}:/usr/src/app" -w /usr/src/app eclipse-temurin:25-jdk-alpine ./mvnw test
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }

        stage('Security Scanning (Trivy)') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo "Scanning the Docker image for vulnerabilities..."
                    sh '''
                    docker run --rm \
                    -v /var/run/docker.sock:/var/run/docker.sock \
                    -v trivy-cache:/root/.cache/trivy \
                    aquasec/trivy:0.49.1 image --timeout 15m --exit-code 0 --severity HIGH,CRITICAL ${DOCKER_IMAGE}:${DOCKER_TAG}
                    '''
                }
            }
        }

        stage('Approval Gate') {
            steps {
                // This will pause the Jenkins pipeline indefinitely until a user clicks 'Proceed' or 'Abort' in the UI
                input message: 'Approve deployment to Production?', ok: 'Deploy Now'
            }
        }

        stage('Deploy to Production') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo "Deploying the containers using Docker Compose..."
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
        
        stage('Health Check') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo "Waiting for 10 seconds to let Spring Boot start..."
                    sleep 10
                    sh "docker ps | grep smart-attendance-backend"
                }
            }
        }
    }
    
    post {
        always {
            echo '==================================='
            echo 'Jenkins Pipeline Finished executing'
            echo '==================================='
        }
        success {
            echo '[SUCCESS] Continuous Integration passes and deployment containers are running!'
        }
        failure {
            echo '[FAILURE] Pipeline failed at some stage. Please check the logs.'
            dir('/Users/arunodayahosakeri/Documents/DevOps/Smart-Attendance-System') {
                sh 'docker-compose logs --tail=50 || true'
            }
        }
    }
}
