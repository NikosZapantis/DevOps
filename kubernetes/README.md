# Kubernetes

## Created images hosted on github:

- `ghcr.io/nikoszapantis/devops-backend:latest`
- `ghcr.io/nikoszapantis/devops-frontend:latest`

**Images were created through docker and pushed on github**

- Access Token:
`.env`

- Command for image generation:

`docker build -t <image>:latest`

# Login through docker
echo "<MY_GITHUB_PAT>" | docker login ghcr.io -u <MY_GITHUB_USERNAME> --password-stdin


# Tagging images

`docker tag backend:latest ghcr.io/nikoszapantis/devops-backend:latest`
`docker tag frontend:latest ghcr.io/nikoszapantis/devops-frontend:latest`

# Pushing images

`docker push ghcr.io/nikoszapantis/devops-backend:latest`
`docker push ghcr.io/nikoszapantis/devops-frontend:latest`
