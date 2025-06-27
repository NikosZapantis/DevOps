# 🐋 Docker Deployment for Crowdfunding Platform

This directory contains the `docker-compose.yaml` file used to containerize and run the full-stack DS Crowdfunding platform. It defines services for the backend (Spring Boot), frontend (Node.js), NGINX reverse proxy and Mailhog (for email testing on future implementation).

All containers are organized via **Docker Compose** and can be deployed either manually or automatically using the `/ansible/docker.yaml` playbook.

---

## 🗃️ Table of Contents

- [Overview](#overview)
- [Services](#services)
- [File Structure](#file-structure)
- [Usage](#usage)
    - [Using Ansible](#using-ansible)
    - [Using Docker Compose manually](#using-docker-compose-manuyally)
- [Container Management](#container-management)
- [Environment Variables](#environment-variables)
- [Troubleshooting](#troubleshooting)

---

## Overview

This setup launches 4 Docker containers:

| Service   | Description                                | Ports               |
|-----------|--------------------------------------------|---------------------|
| `backend` | Spring Boot application container          | `${BACKEND_PORT}`   |
| `frontend`| Node.js + Express static frontend server   | `${FRONTEND_PORT}`  |
| `nginx`   | Reverse proxy to route frontend + backend  | `${NGINX_PORT}`     |
| `mailhog` | Development SMTP/email testing tool        | `1025`, `8025` (UI) |

The `nginx` container uses a config file mounted from the `ansible/reverseproxy/nginx.conf`.

---

## File Structure

```bash
docker/
├── .env [hosting_ports]
├── README.md 
├── docker-compose.yaml 
```

**Related files**:
- **Backend Dockerfile**: `../App/backend/Dockerfile`
- **Frontend Dockerfile**: `../App/frontend/Dockerfile`
- **NGINX config**: `../ansible/reverseproxy/nginx.conf`

---

## Services

### `backend`
- Built from `../App/backend`.
- Java 21 with Maven.
- Compiles Spring Boot project and runs the `.jar` on targer dir.
- Exposes port defined by `${BACKEND_PORT}`.

### `frontend`
- Built from `../App/frontend`.
- Node.js 18.
- Runs `serverInit.js` to serve frontend.
- Uses `${FRONTEND_PORT}` for access.

### `nginx`
- Pulls latest NGINX image.
- Mounts a reverse proxy configuration.
- Connects `/api/` to backend, `/` to frontend.

### `mailhog`
- Mail testing tool (SMTP + Web UI).
- Not fully functional yet — included for future development.
- UI available on port `8025`.

---

## Usage

### 🅰️ Using Ansible

To deploy via Ansible (recommended for automation):

```
ansible-playbook -i inventory.ini ansible/docker.yaml
```

This playbook:
- Installs Docker + Compose plugin.
- Syncs the entire project structure to /home/azureuser/docker/.
- Kills any existing processes on common ports (80, 7000, 8080, 8025).
- Runs `docker compose up -d`.


### 🌐 Using Docker Compose manually
- SSH into your VM:
```
ssh -i ~/.ssh/private_key azureuser@<VM_IP>
cd ~/azureuser/docker/docker
```

- Run containers:
```
docker compose up -d
```

- Stop all containers:
```
docker compose down
```

---

## Container Management
You can manage and inspect the Docker containers via the following commands:

- List containers:
```
docker ps
```

- View logs:
```
docker logs backend
docker logs frontend
docker logs nginx
docker logs mailhog
```

- Stop and remove containers:
```
docker compose down
```

- List images:
```
docker images
```

- Remove all unused containers/images/networks:
```
docker system prune -af
```

---

## Environment Variables
The docker-compose.yaml file depends on a .env file placed in the same directory. You must define the following variables:

```
BACKEND_PORT=<Your_Backend_Port>
FRONTEND_PORT=<Your_Frontend_Port>
NGINX_PORT=<Your_Nginx_Port> (default is 80)
```

Ensure that .env is present next to `docker-compose.yaml` in the remote path:
`/home/azureuser/docker/docker/.env`

---

## Troubleshooting
- **Port already in use**: The Ansible playbook attempts to kill processes on ports 80, 7000, 8080 before running Docker. Verify no other services (PM2/NGINX) are running.
- **Changes not applied**: Use `docker compose down && docker compose up --build -d` to force rebuild.
- **Mailhog not responding**: Currently, Mailhog is included for future use. Make sure it's not blocked by a firewall, and check port 8025.
- **Missing .env**: Containers won't start without the .env file.
