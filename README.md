# Automating-Creation-Of-Service-Pipeline-Using-A-Seed-Job-In-Jenkins

## Overview

This project provides a **fully automated Jenkins CI/CD solution** that dynamically creates and maintains Jenkins pipeline jobs for all repositories within a GitLab group.

The automation is driven by a **Jenkins seed pipeline** and **Job DSL**, enabling zero-manual-effort onboarding for new microservices.

---

## Key Features

- Polls GitLab Group using GitLab REST API
- Automatically creates or updates Jenkins jobs per repository
- Skips archived GitLab projects
- Standardized CI/CD pipelines across services
- Secure credential handling via Jenkins Credentials Store
- Docker image build and push to Docker Hub
- Idempotent and safe job generation

---

## High-Level Architecture

GitLab Group → Jenkins Seed Job → Job DSL → Generated Service Pipelines

Each GitLab repository results in a Jenkins job:

```
services/<service-name>
```

---

## Repository Structure

```
.
├── Jenkinsfile
├── dsl/
│   └── create_service_pipeline.groovy
└── README.md
```

---

## Jenkins Seed Job

The seed job is a Jenkins Pipeline responsible for:

- Authenticating with GitLab
- Fetching all projects under a GitLab group
- Iterating through repositories
- Invoking Job DSL to create/update Jenkins jobs

### Environment Variables

| Variable | Description |
|--------|-------------|
| GITLAB_URL | Base GitLab URL |
| GROUP_ID | GitLab group ID |

---

## GitLab API Authentication

Stored in Jenkins Credentials:

| Credential ID | Type |
|--------------|------|
| gitlab-cred | Secret Text |

Used only by the seed job.

---

## Job DSL: create_service_pipeline.groovy

This DSL script defines a Jenkins Pipeline job template that is reused for all services.

### Pipeline Responsibilities

- Checkout source code
- Build Docker image
- Push image to Docker Hub
- Clean workspace

### Jenkins Job Parameters

| Parameter | Purpose |
|---------|---------|
| REPO_URL | Git repository URL |
| BRANCH | Branch to build |
| REPO_DIR | Directory name after clone |
| IMAGE_NAME | Docker image name |
| DOCKER_REPO | DockerHub organization |
| APP_PORT | Application port |
| CONTAINER_PORT | Container port |

---

## Credential Management (Best Practice)

Secrets are **never passed as parameters**.

Instead, only **credential IDs** are passed from the seed job and resolved at runtime.

### Required Credentials

| Credential ID | Type | Used For |
|--------------|------|---------|
| gitlab-http-cred | Username/Password | Git clone (HTTPS) |
| gitlab-ssh-key | SSH Key | Git clone (SSH) |
| dockerhub-cred | Username/Password | Docker push |

---

## Git Clone Strategies

### SSH (Recommended)

- Secure
- No credentials in command line
- Uses Jenkins ssh-agent

### HTTPS

- Uses username/password or token
- Supported but less preferred

---

## Docker Build and Push

Each service pipeline:

1. Builds Docker image
2. Tags image using Jenkins BUILD_ID
3. Pushes to Docker Hub

Example tag:

```
jhinga/frontend-remit:42
```

---

## Idempotency and Safety

- Jobs are updated if they already exist
- Archived repositories are ignored
- No jobs are deleted automatically
- Workspace is cleaned before and after builds

---

## Adding a New Service

1. Create a repository in the GitLab group
2. Ensure it is not archived
3. Run the Jenkins seed job
4. Jenkins job is created automatically

No Jenkins UI interaction required.

---

## Future Enhancements

- SonarQube integration
- Kubernetes deployments
- Helm chart publishing
- GitLab webhook triggers
- Per-service overrides

---

## License

Internal CI/CD Automation Project
