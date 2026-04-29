pipeline {
    agent any

    environment {
        AWS_REGION         = 'eu-west-1'
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        ECS_CLUSTER        = 'expotrade-staging'
        BACKEND_SERVICE    = 'expotrade-staging-backend'
        FRONTEND_SERVICE   = 'expotrade-staging-frontend'
        BACKEND_TASK_FAMILY  = 'expotrade-staging-backend'
        FRONTEND_TASK_FAMILY = 'expotrade-staging-frontend'
        BACKEND_ECR_REPO   = 'expotrade-staging-backend'
        FRONTEND_ECR_REPO  = 'expotrade-staging-frontend'
        IMAGE_TAG          = "${env.GIT_COMMIT?.take(7) ?: 'latest'}"
    }

    tools {
        jdk 'JDK21'
        nodejs 'Node20'
        maven 'Maven3'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        // ─── Test Backend ───────────────────────────────────────────────
        stage('Test Backend') {
            steps {
                dir('backend') {
                    bat 'mvn clean verify -DskipDocker'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        // ─── Test Frontend ──────────────────────────────────────────────
        stage('Test Frontend') {
            steps {
                dir('frontend') {
                    bat 'npm ci'
                    bat 'npm run build -- --configuration production'
                }
            }
        }

        // ─── SonarQube Analysis ─────────────────────────────────────────
        stage('SonarQube Analysis') {
            environment {
                SONAR_SCANNER_HOME = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat """
                        "${SONAR_SCANNER_HOME}\\bin\\sonar-scanner.bat" ^
                        -Dsonar.projectKey=expotrade ^
                        -Dsonar.projectName=ExpoTrade ^
                        -Dsonar.java.binaries=backend/target/classes
                    """
                }
            }
        }

        // ─── SonarQube Quality Gate ─────────────────────────────────────
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ─── Login to ECR ───────────────────────────────────────────────
        stage('ECR Login') {
            steps {
                script {
                    def accountId = bat(
                        script: '@aws sts get-caller-identity --query Account --output text',
                        returnStdout: true
                    ).trim()
                    env.ECR_REGISTRY = "${accountId}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    bat "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${env.ECR_REGISTRY}"
                }
            }
        }

        // ─── Build & Push Docker Images ─────────────────────────────────
        stage('Build & Push Images') {
            parallel {
                stage('Backend Image') {
                    steps {
                        script {
                            def image = "${env.ECR_REGISTRY}/${BACKEND_ECR_REPO}:${IMAGE_TAG}"
                            def latest = "${env.ECR_REGISTRY}/${BACKEND_ECR_REPO}:latest"
                            bat "docker build -t ${image} -t ${latest} ./backend"
                            bat "docker push ${image}"
                            bat "docker push ${latest}"
                            env.BACKEND_IMAGE = image
                        }
                    }
                }
                stage('Frontend Image') {
                    steps {
                        script {
                            def image = "${env.ECR_REGISTRY}/${FRONTEND_ECR_REPO}:${IMAGE_TAG}"
                            def latest = "${env.ECR_REGISTRY}/${FRONTEND_ECR_REPO}:latest"
                            bat "docker build --build-arg NGINX_CONF=nginx-aws.conf -t ${image} -t ${latest} ./frontend"
                            bat "docker push ${image}"
                            bat "docker push ${latest}"
                            env.FRONTEND_IMAGE = image
                        }
                    }
                }
            }
        }

        // ─── Deploy to ECS ──────────────────────────────────────────────
        stage('Deploy to ECS') {
            parallel {
                stage('Deploy Backend') {
                    steps {
                        script {
                            deployToECS(
                                taskFamily: BACKEND_TASK_FAMILY,
                                service: BACKEND_SERVICE,
                                image: env.BACKEND_IMAGE
                            )
                        }
                    }
                }
                stage('Deploy Frontend') {
                    steps {
                        script {
                            deployToECS(
                                taskFamily: FRONTEND_TASK_FAMILY,
                                service: FRONTEND_SERVICE,
                                image: env.FRONTEND_IMAGE
                            )
                        }
                    }
                }
            }
        }

        // ─── Wait for Stability ─────────────────────────────────────────
        stage('Wait for Stability') {
            steps {
                echo 'Waiting for ECS services to stabilize...'
                bat "aws ecs wait services-stable --cluster ${ECS_CLUSTER} --services ${BACKEND_SERVICE} ${FRONTEND_SERVICE} --region ${AWS_REGION}"
            }
        }

        // ─── Smoke Test (manual — verify ALB DNS in browser) ────────────
        stage('Smoke Test') {
            steps {
                echo "Deployment complete. Verify manually:"
                echo "  Frontend: http://expotrade-staging-alb-1349821291.eu-west-1.elb.amazonaws.com/"
                echo "  Backend:  http://expotrade-staging-alb-1349821291.eu-west-1.elb.amazonaws.com/actuator/health"
            }
        }
    }

    post {
        success {
            echo "Deployment successful! Image tag: ${IMAGE_TAG}"
        }
        failure {
            echo 'Deployment failed. Check the logs above for details.'
        }
        always {
            cleanWs()
        }
    }
}

// ─── Helper: Deploy a service to ECS ────────────────────────────────────────
def deployToECS(Map args) {
    def taskFamily = args.taskFamily
    def service = args.service
    def image = args.image

    // Get current task definition
    bat """
        aws ecs describe-task-definition --task-definition ${taskFamily} --query "taskDefinition" --output json --region ${AWS_REGION} > task-def-${taskFamily}.json
    """

    // Update image and clean metadata fields
    bat """
        powershell -Command "\$td = Get-Content 'task-def-${taskFamily}.json' | ConvertFrom-Json; \$td.containerDefinitions[0].image = '${image}'; \$td.PSObject.Properties.Remove('taskDefinitionArn'); \$td.PSObject.Properties.Remove('revision'); \$td.PSObject.Properties.Remove('status'); \$td.PSObject.Properties.Remove('requiresAttributes'); \$td.PSObject.Properties.Remove('compatibilities'); \$td.PSObject.Properties.Remove('registeredAt'); \$td.PSObject.Properties.Remove('registeredBy'); \$td | ConvertTo-Json -Depth 10 | Set-Content 'new-task-def-${taskFamily}.json'"
    """

    // Register new task definition
    bat "aws ecs register-task-definition --cli-input-json file://new-task-def-${taskFamily}.json --region ${AWS_REGION}"

    // Update service
    bat "aws ecs update-service --cluster ${ECS_CLUSTER} --service ${service} --task-definition ${taskFamily} --force-new-deployment --region ${AWS_REGION}"
}
