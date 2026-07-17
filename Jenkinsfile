pipeline {
    agent any

    tools {
        // Ensure you have these tools configured in Jenkins under "Global Tool Configuration"
        // Replace 'Maven' and 'JDK 17' with the exact names configured in your Jenkins instance.
        maven 'Maven'
        jdk 'JDK 17'
    }

    environment {
        DOCKER_IMAGE_NAME = 'healthwatch-ai'
        // If you're pushing to a registry, uncomment and update these:
        // DOCKER_REGISTRY = 'your-registry-url'
        // DOCKER_CREDENTIALS_ID = 'your-docker-credentials-id'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checks out source code from the SCM configured in the Jenkins job
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                // Since this is a Spring Boot/Maven project, we compile and package
                // Use 'sh' for Linux/macOS agents, or 'bat' for Windows agents.
                // If you use Windows agents, change 'sh' to 'bat'.
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {
                    // Requires the Docker Pipeline plugin to be installed in Jenkins
                    dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${env.BUILD_ID}")
                }
            }
        }

        // Optional: Stage for pushing the Docker image
        /*
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        dockerImage.push()
                        dockerImage.push('latest')
                    }
                }
            }
        }
        */
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
