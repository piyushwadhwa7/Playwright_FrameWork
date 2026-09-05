// ---------------------------------------------------------------------------
// Declarative Jenkins pipeline for Playwright_FrameWork.
// Modelled on the Naveen Automation Labs reference pipeline.
//
// Paste this whole file into the "Pipeline script" box:
//   New Item -> Pipeline -> Pipeline script.
//
// Stages: Checkout -> Build -> Deploy to QA -> Regression Automation Test -> Publish Report -> Qodana
// Runs mvn directly (Playwright uses in-process browsers) — no Selenium grid.
// ---------------------------------------------------------------------------

pipeline {
    agent any

    tools {
        // IMPORTANT: this name must match a Maven installation configured under
        // Manage Jenkins -> Tools -> Maven installations. Your Jenkins already
        // has one named "MAVEN" (seen in earlier build logs). If yours is named
        // differently, change the value below to match.
        maven 'MAVEN'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -DskipTests clean package'
            }
            post {
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('Deploy to QA') {
            steps {
                echo 'deploy to qa'
            }
        }

        stage('Regression Automation Test') {
            steps {
                // catchError keeps the overall build GREEN even if a test fails,
                // but marks THIS stage red — same behaviour as the reference.
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    withCredentials([
                        usernamePassword(credentialsId: 'opencart-login', usernameVariable: 'OC_USERNAME', passwordVariable: 'OC_PASSWORD'),
                        string(credentialsId: 'gorest-token', variable: 'GOREST_TOKEN')
                    ]) {
                        // Run the TestNG suite headless (no display needed on the agent).
                        sh '''
                            mvn clean test \
                                -Dsurefire.suiteXmlFiles=src/test/resources/TestRunners/testng_regression.xml \
                                -Dheadless=true \
                                -Dusername="$OC_USERNAME" \
                                -Dpassword="$OC_PASSWORD" \
                                -Dgorest.bearer.token="$GOREST_TOKEN"
                        '''
                    }
                }
            }
        }

        stage('Publish Report') {
            steps {
                // TestNG results are captured through allure-testng and published by Allure.
                allure includeProperties: false, results: [[path: 'allure-results']]
            }
        }
        stage('Qodana') {
            agent {
                docker {
                    args '''
                        -v "${WORKSPACE}":/data/project
                        -v "${WORKSPACE}/qodana-results":/data/results
                        --entrypoint=""
                        '''
                    image 'jetbrains/qodana-jvm-community:2026.2'
                }
            }
            steps {
                sh '''
                    unset QODANA_TOKEN
                    qodana
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'qodana-results/**', allowEmptyArchive: true
                }
            }
        }
    }
}
