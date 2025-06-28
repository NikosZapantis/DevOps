# 🧩 Jenkins CI/CD Pipeline for DS Crowdfunding Platform

This directory contains the CI/CD configuration for automating deployment, testing and containerization of the DS Crowdfunding platform using **Jenkins**, **Ansible**, and optionally **Docker/Kubernetes**.

---

## 🗃️ Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [VM Setup](#vm-setup)
- [Ports and Services](#ports-and-services)
- [Pipeline Jobs](#pipeline-jobs)
    - [ansible.Jenkinsfile](#ansiblejenkinsfile)
    - [Jenkinsfile (Docker + K8s)](#jenkinsfile-docker--k8s)
- [Usage](#usage)
- [Pipeline Flow](#pipeline-flow)
- [Troubleshooting](#troubleshooting)

---

## Overview

The jenkins CI/CD setup includes:

- Automatic deployment of frontend and backend via **Ansible**.
- Custom parameterized pipelines.
- Docker image build and push to Github Container Registry.
- Optional Kubernetes deployment.
- Ansible playbooks triggered via Jenkins pipelines.
- Branch-based automation: the pipeline pulls from `ansible_only` or `jenkins` branches.

---

## Requirements

- Jenkins installed and running on a VM.
- Port `8080` open for Jenkins UI.
- Jenkins user (`jenkins`) with password or token-based auth.
- Jenkins plugins installed:
  - **Pipeline**
  - **Git**
  - **Ansible**
- GitHub credentials configured in Jenkins (for cloning & pushing).
- Docker installed in Jenkins host (if using containerization).
- `.ssh/<SSH_PRIVATE_KEY>` deployed to Jenkins and target VMs.

---

## VM setup

A new VM was created for Jenkins. The following was performed:

1. Enabled ports:
    - `8080` → Jenkins
    - `8081` → Spring Boot backend
    - `7000` → Node.js frontend
    - `80`   → NGINX

2. Run:
    - `install_packages.yaml` (installs Ansible via venv).
    - `custom_commands_jenkins.yaml` (uploads SSH keys, known_hosts).

3. Cloned repo branch `ansible_only` inside Jenkins workspace.

---

## Ports and Services

| Port         | Service                     |
|--------------|-----------------------------|
| `8080`       | Jenkins UI                  |
| `8081`       | Spring Boot app             |
| `7000`       | Node.js frontend            |
| `80`         | NGINX reverse proxy         |
| `1025 / 8025`| (Mailhog - optional)        |

---

## Pipeline Jobs

### `ansible.Jenkinsfile`

This pipeline automates Ansible-based deployment.

### Features:

- Parameterized flags to enable/disable parts of the deployment:
  - `INSTALL_SPRING`, `INSTALL_NODE`, `INSTALL_POSTGRES`.
- Pings infrastructure to ensure SSH access.
- Calls `ansible-job` for orchestrated deployments.
- Deploys backend (`spring.yaml`) and frontend (`node.yaml`) via Ansible.


### `Jenkinsfile (Docker + K8s)`

This pipeline builds Docker images, pushes them to GitHub Container Registry and deploys to Kubernetes via Ansible (Manually for now).

### Features:

- Builds multi-stage Docker image with dynamic tag (commit + build ID).
- Pushes to GHCR using Jenkins credentials (`docker-push-secret`).
- Deploys new image to Kubernetes using:
    - `playbooks/k8s-update-spring-deployment.yaml`
    - with `-e new_image=$TAG` Ansible variable.

---

## Usage
1. Go to Jenkins UI (`http://<VM_IP>:8080`).
2. Trigger the ansible-job pipeline manually or from the main Jenkinsfile.
3. Optionally pass parameters to enable/disable backend/frontend deployment.
4. Monitor logs in each pipeline stage.

---

## Pipeline Flow

```mermaid
graph TD
    A[Code pushed to ansible_only branch] --> B[Ansible Pipeline (ansible.Jenkinsfile)]
    B --> C[Test Ansible availability]
    C --> D[Run ansible-job]
    D --> E[Test SSH ping to VM]
    E --> F{Deploy Backend?}
    F -- Yes --> G[Run spring.yaml via Ansible]
    F -- No --> H[Skip Spring]
    G --> I{Deploy Frontend?}
    I -- Yes --> J[Run node.yaml via Ansible]
    I -- No --> K[Skip Node]
    J --> L[✅ Deployment Complete]
    K --> L
    H --> I
```

---

## Troubleshooting
- **Ansible not found**: Ensure `install_packages.yaml` ran and symlinks were created and check that Jenkins uses the correct Ansible.
- **Permission denied (SSH)**: Ensure correct private key (`~/.ssh/<SSH_PRIVATE_KEY>`) is loaded in Jenkins and validate `inventory.ini`.
- **Docker build issues**: Make sure Docker is installed and running on the Jenkins host and check for correct file path to `Dockerfile`.
- **Git error / branch not found**: Verify that Jenkins is checking out the correct branch (`ansible_only`).


