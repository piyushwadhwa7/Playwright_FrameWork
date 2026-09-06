# Jenkins Pipeline and Real Deployment Guide

This document explains the Jenkins pipeline in this project, what each stage does, where Docker is used, and how a similar pipeline works in a real QA and production environment.

## 1. Current Pipeline at a Glance

The current pipeline in `Jenkinsfile` runs in this order:

```text
Developer pushes code
        |
        v
Jenkins starts a build
        |
        v
Checkout -> Build -> Deploy to QA (placeholder) -> Regression Tests
        |
        v
Publish Allure Report -> Run Qodana -> Archive Qodana results
```

Important: the current `Deploy to QA` stage does **not** deploy an application. It only prints `deploy to qa`. The remaining stages build and test this automation framework.

## 2. Step-by-Step Explanation of the Current Jenkinsfile

### Step 1: Select a Jenkins Agent

```groovy
agent any
```

Jenkins chooses an available agent machine to run the pipeline. In a small setup, this may be the Jenkins server itself. In a real project, it is normally a separate Linux VM, a cloud runner, or an ephemeral Kubernetes agent.

### Step 2: Configure Maven

```groovy
tools {
    maven 'MAVEN'
}
```

Jenkins loads the Maven installation named `MAVEN` from **Manage Jenkins -> Tools**. The name must exactly match the configured Jenkins tool name.

### Step 3: Checkout Source Code

```groovy
stage('Checkout') {
    steps {
        checkout scm
    }
}
```

`checkout scm` downloads the source code from the Git repository and checks out the branch that triggered the build. `scm` means Source Control Management, usually GitHub, GitLab, or Bitbucket.

### Step 4: Build the Project

```groovy
mvn -DskipTests clean package
```

This command removes old build output, compiles the Java project, downloads required Maven dependencies, and packages the project. Tests are deliberately skipped at this point because they run in the later regression stage.

On success, Jenkins stores matching JAR files from `target/*.jar` as build artifacts. Artifacts allow a team to download the exact build output later.

### Step 5: Deploy to QA (Current Placeholder)

```groovy
stage('Deploy to QA') {
    steps {
        echo 'deploy to qa'
    }
}
```

This is only a placeholder. It does not copy files, start Docker containers, update Kubernetes, or change a QA environment. A real deployment command would replace the `echo` line.

### Step 6: Run Regression Automation Tests

Jenkins securely loads these credentials:

- `opencart-login`: application username and password
- `gorest-token`: bearer token for the GoRest API tests

It then runs:

```bash
mvn clean test \
  -Dsurefire.suiteXmlFiles=src/test/resources/TestRunners/testng_regression.xml \
  -Dheadless=true \
  -Dusername="$OC_USERNAME" \
  -Dpassword="$OC_PASSWORD" \
  -Dgorest.bearer.token="$GOREST_TOKEN"
```

This starts the TestNG regression suite. `-Dheadless=true` lets Playwright run browser tests without a visible desktop window, which is required on most Jenkins agents. The same suite can run UI tests and API tests if both are listed in `testng_regression.xml`.

The `catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE')` block means a test failure marks the **Regression Automation Test** stage as failed but keeps the overall Jenkins build successful. This lets later report stages still execute. Teams should use this behavior intentionally: it is useful for scheduled test reporting, but a release pipeline usually makes production deployment fail when critical tests fail.

### Step 7: Publish the Allure Report

```groovy
allure includeProperties: false, results: [[path: 'allure-results']]
```

The Allure Jenkins plugin reads the `allure-results` folder created by the TestNG listener and publishes a browser-viewable report in Jenkins. The report shows passed/failed tests, steps, attachments, and trends when history is persisted.

### Step 8: Run Qodana Static Analysis

```groovy
agent {
    docker {
        image 'jetbrains/qodana-jvm-community:2026.2'
    }
}
```

For this stage, Jenkins starts a temporary Qodana Docker container **on the Jenkins agent machine**. Jenkins mounts the repository into `/data/project` and writes analysis output to `${WORKSPACE}/qodana-results`. The command `qodana` scans the Java project for code-quality issues, then the container exits. This container is not your deployed application.

## 3. Local Docker vs Real Project Docker

When you run Docker locally, the lifecycle is simple:

```text
Laptop -> docker build -> docker run -> container runs on your laptop
```

In a real project, the pipeline generally does this:

```text
Developer push
  -> Jenkins build and tests
  -> Docker image build
  -> Push image to a private registry
  -> QA or production platform pulls the approved image
  -> Platform starts the new application container
  -> Health checks and smoke tests verify it
```

The image registry is commonly Docker Hub, AWS ECR, Azure Container Registry, Google Artifact Registry, JFrog Artifactory, or GitLab Container Registry.

## 4. Common Real Deployment Models

### A. Docker on a Virtual Machine

Jenkins connects to a QA or production Linux server using an SSH key. Docker runs on that remote server, not on the Jenkins machine and not on a developer laptop.

```bash
docker pull registry.company.com/my-app:125
docker stop my-app || true
docker rm my-app || true
docker run -d --name my-app -p 8080:8080 registry.company.com/my-app:125
```

The exact server is different for QA, staging, and production. Secrets are injected through server-side environment variables, a secret manager, or protected Jenkins credentials.

### B. Docker Compose on a Virtual Machine

This is common for small and medium systems with several services, such as an application, database, Redis, and a message queue.

```bash
docker compose pull
docker compose up -d
```

Docker Compose starts or updates the defined services on the remote host.

### C. Kubernetes

Kubernetes is common for larger systems. Jenkins pushes an image to a registry and updates the Kubernetes deployment. Kubernetes worker nodes pull the image and run the containers.

```bash
kubectl set image deployment/my-app \
  my-app=registry.company.com/my-app:125 \
  --namespace qa
kubectl rollout status deployment/my-app --namespace qa
```

Here, you normally do not run `docker run` yourself. Kubernetes handles starting, restarting, scaling, and replacing containers.

### D. Managed Container Platforms

AWS ECS/Fargate, Azure Container Apps, Google Cloud Run, and similar services run containers for the team. Jenkins updates the image version or service definition, and the cloud platform pulls and starts the image.

## 5. Real QA and Production Pipeline Flow

```text
1. Developer creates a pull request
2. CI runs compile, unit tests, API/UI tests, and static analysis
3. Code review is approved and changes merge to the main branch
4. Jenkins builds one versioned Docker image, for example my-app:1.8.0-125
5. Jenkins pushes that exact image to the private registry
6. Jenkins deploys the image to QA
7. Smoke and regression tests run against QA
8. A release approval or automated quality gate allows production deployment
9. Jenkins deploys the same already-tested image to production
10. Health checks, monitoring, logs, and alerts verify the rollout
11. The pipeline rolls back to the previous stable image if the deployment fails
```

The key principle is: **build once, promote the same image**. QA and production should receive the same image tag, rather than rebuilding separately for each environment.

## 6. Example Production-Style Deployment Commands

### Build and Push Image

```bash
docker build -t registry.company.com/my-app:${BUILD_NUMBER} .
docker push registry.company.com/my-app:${BUILD_NUMBER}
```

### Deploy to QA with Kubernetes

```bash
kubectl set image deployment/my-app \
  my-app=registry.company.com/my-app:${BUILD_NUMBER} \
  --namespace qa
kubectl rollout status deployment/my-app --namespace qa
curl --fail --retry 10 --retry-delay 5 https://qa.example.com/actuator/health
```

### Deploy to Production with a Manual Approval

```groovy
input message: 'Approve production deployment?', ok: 'Deploy'
```

After approval, the pipeline deploys the already-tested image tag to the production namespace or production servers.

## 7. Production Standards to Follow

- Keep passwords, API tokens, SSH keys, cloud keys, and registry credentials in Jenkins Credentials or a secret manager. Never hard-code them in the `Jenkinsfile`.
- Use separate QA, staging, and production configuration. Do not use production credentials in QA tests.
- Add a health check after every deployment and fail or roll back when it is unhealthy.
- Tag images with an immutable build number or Git commit SHA; avoid deploying `latest`.
- Archive test results, Allure reports, Docker image tags, and deployment logs as build evidence.
- Use role-based approval before production deployment when required by the team or compliance rules.
- Prefer short-lived Jenkins agents for reliable and repeatable builds.
- Preserve Allure history in persistent storage if cross-run trends are required.

## 8. What Your Current Pipeline Does and Does Not Do

| Area | Current Jenkinsfile | Real deployment pipeline |
| --- | --- | --- |
| Build | Builds Maven project | Builds application artifact and/or Docker image |
| QA deployment | Placeholder only | Updates QA server, Kubernetes, or cloud service |
| UI/API tests | Runs TestNG regression suite | Runs before release and often after QA deployment |
| Allure | Publishes current results | Publishes results and persists history/trends |
| Qodana | Runs in a temporary Docker container on Jenkins agent | Same pattern is common for static analysis |
| Production deployment | Not present | Uses approval/gates, deploys versioned image, health-checks, and supports rollback |

## 9. Recommended Next Improvement for This Project

Keep the present pipeline for automation testing. When an actual application deployment target exists, replace the QA placeholder with the deployment approach used by that application team: SSH/Docker Compose, Kubernetes, or a managed cloud container platform. Add a post-deployment smoke test stage that runs only after the QA health check succeeds.
