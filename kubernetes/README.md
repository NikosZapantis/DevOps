# 🚀 Kubernetes Deployment Guide

This directory contains the configuration and setup files for building, tagging, and deploying the frontend and backend components of the application using **Docker** and **Kubernetes**. Docker images are hosted on **GitHub Container Registry (GHCR)**, and the deployment is managed through standard **Kubernetes manifests**.

---

## 🗃️ Table of Contents
- [Overview](#overview)
- [Directory Stucture](#directory-structure)
- [Requirements](#requirements)
- [Vm Setup](#vm-setup)
- [Ports and Services](#ports-and-services)
- [Buid & Push Docker Images](#build--push-docker-images)
- [Kubernetes manifest files](#kubernetes-manifest-files)
    - [cluster-issuer.yaml](#cluster-issuer.yaml)
    - [js-deployment.yaml](#js-deployment.yaml)
    - [js-ingress.yaml](#js-ingress.yaml)
    - [js-svc.yaml](#js-svc.yaml)
    - [spring-deployment.yaml](#spring-deployment.yaml)
    - [spring-ingress.yaml](#spring-ingress.yaml)
    - [spring-svc.yaml](#spring-svc.yaml)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)

---

## Overview

The Kubernetes setup includes:

- Docker images pulled directly from **GitHub Container Registry (GHCR)**.
- Separation of manifests into folders: `spring/`, `js/`, and `cert/`.
- Separate deployment files (`Deployment`, `Service`, `Ingress`) for each component.
- Deployment of both frontend and backend applications using Kubernetes manifests.
- Manual deployment via `kubectl`.

---

## Directory Structure

```bash
kubernetes/
├── cert/ 
│ └── cluster-issuer.yaml
├── js/ 
│ └── js-deployment.yaml
│ └── js-ingress.yaml
│ └── js-svc.yaml
├── spring/
│ └── spring-deployment.yaml
│ └── spring-ingress-ssl.yaml
│ └── spring-ingress.yaml
│ └── spring-svc.yaml
```
---

## Requirements

- **VM with Ubuntu 20.04 or 22.04**
- **MicroK8s installed via Snap**
- **Public IP and open port 16443**
- **SSH access and proper firewall rules**
- **kubectl installed locally or via MicroK8s**

---

## VM Setup

A new VM was created for Kubernetes.

**VM Specifications**
```ini
Standard B2s (2 vcpus, 4 GiB memory) (Azure)
E2-standard-2 (2 vCPU, 1 core, 8GB memory)
```
The following was performed:

1. Enabled ports:
    - `16443` → Kubernetes
    - `8080` → Spring Boot backend
    - `7000` → Node.js frontend
    - `80`   → NGINX
2. Establish connection to the vm via ssh <username>@<vm-ip-address>
3. sudo snap install microk8s --classic
4. sudo ufw allow in on ethh0 && sudo ufw allow out on eth0
5. sudo ufw default allow routed
6. microk8s.enable dns storage ingress
7. Clone repo `https://github.com/NikosZapantis/DevOps.git` inside Kubernetes workspace.

---

## Ports and Services

| Port         | Service                     |
|--------------|-----------------------------|
| `16443`      | Kuberenetes                 |
| `8080`       | Spring Boot app             |
| `7000`       | Node.js frontend            |
| `80`         | NGINX reverse proxy         |
| `1025 / 8025`| (Mailhog - optional)        |

---

## Kubernetes manifest files

**cluster-issuer.yaml**
Defines a self-signed ClusterIssuer for TLS certificate generation via cert-manager.

**js-deployment.yaml**
Automates deployment of the Node.js frontend:

    - Creates a Kubernetes Deployment for the frontend application.
    - Pulls the Docker image from GitHub Container Registry.
    - Sets CPU and memory resource requests and limits.
    - Exposes container port 80 to be used by the Service or Ingress.
    - Runs a single replica under the default namespace.

**js-ingress.yaml**
Configures Ingress for the Node.js frontend:

    - Defines an HTTP rule to route traffic from / to the frontend service.
    - Uses the service name js-svc and forwards to its named port js.
    - Path type is set to ImplementationSpecific for flexible matching.

**js-svc.yaml**
Defines the Service for the Node.js frontend:

    - Exposes port 80 internally within the cluster.
    - Routes traffic to pods labeled with app: js on target port 7000.
    - Uses ClusterIP type for internal access only.
    - Named port js for reference by Ingress or other resources.

**spring-deployment.yaml**
Deploys the Spring Boot backend application:

    - Creates a Deployment with one replica running the backend container.
    - Pulls the Docker image from GitHub Container Registry.
    - Configures liveness and readiness probes for health monitoring.
    - Sets resource requests and limits for CPU and memory.
    - Injects database connection details via environment variables.
    - Exposes container port 8080 for service communication.

**spring-ingress-ssl.yaml**
Configures HTTPS Ingress for the Spring Boot backend:

    - Routes incoming traffic on host demo.local to the backend service.
    - Uses TLS with the secret spring-tls-secret to enable SSL termination.
    - Rewrites requests to / path using NGINX ingress annotation.
    - Forwards traffic to the service named spring on port spring.

**spring-ingress.yaml**
Configures HTTP Ingress routing for backend and frontend services:

    - Routes requests with path prefix /api/ to the Spring backend service (spring-svc) on port 8080.
    - Routes all other requests (/) to the frontend service (js-svc) on port 80.
    - Uses flexible path matching with ImplementationSpecific pathType for frontend.
    - Enables unified ingress point for both frontend and backend under the same domain.

**spring-svc.yaml**
Defines the Service for the Spring Boot backend:

    - Exposes port 8080 internally within the Kubernetes cluster.
    - Routes traffic to pods labeled app: spring on target port 8080.
    - Uses ClusterIP type, allowing internal cluster communication only.
    - Named port spring for referencing in Ingress or other resources.

---

## Build & Push Docker Images

### Created images hosted on github:

- `ghcr.io/nikoszapantis/devops-backend:latest`
- `ghcr.io/nikoszapantis/devops-frontend:latest`

**Images were created through docker and pushed on github**

- Access Token:
`.env`

- Command for image generation:

`docker build -t <image>:latest`

### Login through docker
echo "<MY_GITHUB_PAT>" | docker login ghcr.io -u <MY_GITHUB_USERNAME> --password-stdin


### Tagging images

`docker tag backend:latest ghcr.io/<githubusername>/devops-backend:latest`
`docker tag frontend:latest ghcr.io/<githubusername>/devops-frontend:latest`

### Pushing images

`docker push ghcr.io/<githubusername>/devops-backend:latest`
`docker push ghcr.io/<githubusername>/devops-frontend:latest`

---

## Usage

1. Establish connection to the vm via ssh <username>@<vm-ip-address>
2. alias `k=microk8s.kubectl`
3. cd `DevOps/kubernetes` to deploy Spring Boot backend & frontend
```
    - k apply -f ./spring/spring-deployment.yaml
    - k apply -f ./spring/spring-svc.yaml
    - k apply -f ./spring/spring-ingress.yaml
    - k apply -f ./js/js-deployment.yaml
    - k apply -f ./js/js-svc.yaml
```
4. Run k get pod -A to ensure all pods are up and running **[might take up to two (2) minutes]**
5. Run k get svc to ensure all services are up and running **[might take up to two (2) minutes]**
6. Run k get ingress to ensure the ingress is up and running **[might take up to two (2) minutes]**
7. Open a browser and go to <vm-ip-address> or <vm-ip-address>:80

---

## Troubleshooting
- **Pods not up and running on first try**: Try running the command after 1-2 minutes and everything should be working.
