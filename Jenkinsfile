// ---------------------------------------------------------------------------
// Declarative Jenkins pipeline for Playwright_FrameWork.
// Modelled on the Naveen Automation Labs reference pipeline.
//
// Paste this whole file into the "Pipeline script" box:
//   New Item -> Pipeline -> Pipeline script.
//
// Stages: Build -> Deploy to QA -> Regression Automation Test -> Publish Report
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

        stage('Build') {
            steps {
                // Demo repo from the tutorial — a simple Maven project with tests.
                git 'https://github.com/jglick/simple-maven-project-with-tests.git'
                sh 'mvn -Dmaven.test.failure.ignore=true clean package'
            }
            post {
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
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
                    // Your framework repo (branch main — it has no 'master').
                    git branch: 'main',
                        url: 'https://github.com/piyushwadhwa7/Playwright_FrameWork'
                    // Run the TestNG suite headless (no display needed on the agent).
                    sh 'mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/TestRunners/testng_regression.xml -Dheadless=true'
                }
            }
        }

        stage('Publish Report') {
            steps {
                // Framework produces Allure reports (Extent was never wired up).
                allure includeProperties: false, results: [[path: 'allure-results']]
            }
        }
    }
}
