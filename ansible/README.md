# 🅰️ Ansible Deployment Automation

This directory contains a collection of Ansible playbooks and configurations designed to provision and deploy a full-stack web application {Crowdfunding platform from DS} composed of a Spring Boot backend and a Node.js frontend. It supports environments using Docker, systemd services, and Jenkins automations.

---

## 🗃️ Table of Contents

- [Overview](#overview)
- [Directory Structure](#directory-structure)
- [Requirements](#requirements)
- [Inventory Configuration](#inventory-configuration)
- [Playbooks](#playbooks)
    - [node.yaml](#nodeyaml)
    - [spring.yaml](#springyaml)
    - [docker.yaml](#dockeryaml)
    - [install_packages.yaml](#install_packagesyaml)
    - [custom_commands_jenkins.yaml](#custom_commands_jenkinsyaml)
- [Templates](#templates)
- [Host Variables](#host-variables)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)

---

## Overview

This ansible automation system supports:

- Node.js/PM2/NGINX-based deployment for frontend apps.
- Spring Boot application deployment with Maven + systemd integration.
- Docker-based alternative deployment for both frontend and backend.
- Jenkins integration with SSH key configuration.
- Local Ansible installation via Python env.

---

## Directory Structure

```bash
ansible/
├── group_vars/ 
│ └── general.yaml
├── playbooks/
│ └── custom_commands_jenkins.yaml
│ └── docker.yaml 
│ └── install_packages.yaml
│ └── k8s-update-spring-deployment.yaml 
│ └── node.yaml
│ └── spring.yaml
├── reverseproxy/
│ └── nginx.conf 
│ └── nginx.node.https.j2 
│ └── nginx.node.j2 
│ └── spring.service.j2 
├── templates/
│ └── application.properties.j2
├── inventory.ini
├── README.md
```

---

## Requirements

- **Ansible >= 2.10**
- **Python >= 3.6**
- Ubuntu VMs (on Azure) or local linux VMs.
- SSH access and proper inventory configuration.

---

## Inventory Configuration

Ansible connects to target machines using the `inventory.ini` file:

```ini
[general]
cf_devops ansible_host=<VM_IP> ansible_user=<VM_USER> ansible_ssh_private_key_file=<LOCAL_PATH_TO_SSH_PRIVATE_KEY>
```

---

## Playbooks

**node.yaml**
Automates deployment of the Node.js frontend:

    - Installs Node.js and NGINX.
    - Syncs frontend src code from local to remote VM.
    - Installs dependencies and runs serverInit.js using PM2.
    - Configures NGINX reverse proxy using Jinja2 template.
    - Enables access control permissions for static files

- Usage:
    `ansible-playbook -i inventory.ini ./playbooks/node.yaml`

**spring.yaml**
Automates deployment of the Spring Boot backend:

    - Installs OpenJDK 21.
    - Clones Spring Boot repo from GitHub.
    - Injects application.properties manually.
    - Builds the project using Maven Wrapper.
    - Registers it as a systemd service using spring.service.j2.
    - Supports Maven SHA512 rescue in case of verification failure.

- Usage:
    `ansible-playbook -i inventory.ini ./playbooks/spring.yaml`

**docker.yaml**
Alternative method for deploying both frontend and backend using Docker Compose (includes nginx container & mailhog):

    - Installs Docker & Docker Compose Plugin.
    - Syncs all project files (frontend/backend/docker).
    - Stops previous containers and clears ports (7000, 8080, 80).
    - Runs docker compose up -d.

- Usage:
    `ansible-playbook -i inventory.ini ./playbooks/docker.yaml`


**install_packages.yaml** [Automation_Jenkins]
Bootstraps a fresh VM with the latest version of Ansible using Python virtual environments:

    - Installs python3 and venv.
    - Creates a virtualenv in /opt/ansible_venv.
    - Installs Ansible using pip.
    - Symlinks ansible and ansible-playbook to /usr/local/bin.

- Usage:
    `ansible-playbook -i inventory.ini ./playbooks/install_packages.yaml`

**custom_commands_jenkins.yaml** [Automation_Jenkins]
Prepares a Jenkins instance to interact with remote VMs via SSH:

    - Uploads an SSH private key to /var/lib/jenkins/.ssh/.
    - Adds the target host to known_hosts.
    - Ensures secure permissions are applied.

- Usage:
    `ansible-playbook -i inventory.ini ./playbooks/custom_commands_jenkins.yaml`

---

## Templates

**reverseproxy/nginx.node.j2**:
Reverse proxy configuration for frontend serving via NGINX.

**reverseproxy/nginx.node.https.j2**:
Reverse proxy configuration for frontend serving via NGINX, specific for HTTPS connection.

**files/spring.service.j2**:
Systemd unit file to run the Spring Boot backend as a Linux service.

**templates/application.properties.j2**:
Spring Boot config template with variables for app settings, database, security, and JWT.

---

## Host Variables

**group_vars/general.yaml**: Holds variables specific for VMs under the general group.

---

## Usage
Run those (ansible only manual deployment):
```
ansible-playbook -i inventory.ini ./playbooks/spring.yaml
ansible-playbook -i inventory.ini ./playbooks/node.yaml
```

Run those (ansible docker deployment):
```
ansible-playbook -i inventory.ini ./playbooks/docker.yaml
```

Run those (on jenkins deployment for ansible download and ssh setup on VM):
```
ansible-playbook -i inventory.ini ./playbooks/install_packages.yaml
ansible-playbook -i inventory.ini ./playbooks/custom_commands_jenkins.yaml
```

---

## Troubleshooting
- **Permission Denied on SSH**: Check that the SSH private key path in inventory.ini is valid and accessible by your local.
- **Ports already in use**: Playbooks automaticall kill services using ports 80,7000 and 8080 but have in mind that they may are in use by other job.
- **Ports not exist**: Check if ports are available on your VM or a firewall is enabled.
