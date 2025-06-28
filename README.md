## DevOps Crowdfunding Project

---

## 👨‍💻 Authors

- [🔗 Zapantis Nikolaos](https://github.com/NikosZapantis)
- [🔗 Lymperi Alexandra](https://github.com/alexandralymperi)

---

## 🗃️ Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)

---

## Overview

This repository contains a complete DevOps pipeline for deploying and managing a **Crowdfunding Web Application** with:

- **♨️ Java Spring Boot backend**
- **｡🇯‌🇸 JavaScript / Node.js frontend**

The system is deployed on an **Azure Virtual Machine** using:

- [🅰️ Ansible](https://www.ansible.com/) for server provisioning & automation.
- [🐋 Docker](https://www.docker.com/) for containerization.  
- [🧩 Jenkins](https://www.jenkins.io/) for CI/CD pipelines.  
- [☸️ Kubernetes](https://kubernetes.io/) for kubernetes deployment.  

---

## Project Structure [README files]

├── [App/](App/README.md)  
├── [ansible/](ansible/README.md)  
├── [docker/](docker/README.md)  
├── [jenkins/](jenkins/README.md)  
├── [kubernetes/](kubernetes/README.md)  
└── [README.md](README.md)  

---

## Architecture

- Ansible automates the provisioning of the server, package installations, and deployment process.
- Docker containers are used to package both the backend and frontend services (+ external, such as mailhog).
- Jenkins manages the CI/CD pipeline with both `Jenkinsfile` and `ansible.Jenkinsfile`.
- Kubernetes organize the application in containers across the Azure VM.
- Azure VM serves as the host environment for all deployments.

---

## Installation

1. Create an Azure Virtual Machine with a Linux image (latest ideally).
2. Clone this repository on your local machine.
3. Configure:
    - ansible/inventory.ini

4. Run the Ansible playbook to deploy the Spring Boot + Node.js application:

```
ansible-playbook -i ansible/inventory.ini ansible/playbooks/spring.yaml
ansible-playbook -i ansible/inventory.ini ansible/playbooks/node.yaml
```

---

## Usage

Once deployed:
- 🌐 The Spring Boot REST API runs on the Azure VM's public IP.
- 🖥️ The frontend communicates with the backend via HTTP/HTTPS.
- 🔁 A reverse proxy (NGINX) is set up for routing traffic.

---

## Technologies Used

- 🅰️ Ansible
- 🐋 Docker
- 🧩 Jenkins
- ☸️ Kubernetes
- 🖥️ Azure
- ♨️ Java 21, 🌱 Spring Boot (backend)
- **</>** HTML, 🎨 CSS, **｡🇯‌🇸**‌ JavaScript (frontend)

