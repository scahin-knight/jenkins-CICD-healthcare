pipeline {
    // We use 'agent any' at the top level to allocate a Jenkins node and workspace.
    // We will use a Docker container specifically for the Maven Build and Test stages.
    agent any

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

        // Grouping Maven stages to run inside a Maven Docker container
        stage('Maven Build & Test') {
            agent {
                docker {
                    // Using official Maven Docker image with JDK 17
                    image 'maven:3.9.4-eclipse-temurin-17'
                    // reuseNode true ensures it uses the same workspace checked out above
                    reuseNode true
                    // Map the local Maven repository to cache dependencies between builds
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            stages {
                stage('Build') {
                    steps {
                        echo 'Building the application inside Docker...'
                        sh 'mvn clean package -DskipTests'
                    }
                }

                stage('Test') {
                    steps {
                        echo 'Running tests inside Docker...'
                        sh 'mvn test'
                    }
                    post {
                        always {
                            // Publish JUnit test results
                            junit 'target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }

        stage('Build Docker Image') {
            // This runs on the base node (which has Docker CLI installed)
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
