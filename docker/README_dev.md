# How to execute (example):

At dir with path: /docker:
    `docker compose up --build`

- Basically it sets up all 3 main components:
    - Frontend
    - Backend
    - Postgres db

It's already tested it works fine.

# The logic followed

- A `docker-compose.yaml` at **/docker** that basically is responsible to docker up all the components one by one (included the mailhog with future implementation if needed).
- Dockerfiles inside **/App/backend** & **/App/frontend** that each of them uses the needed packages and deploy the localhosts with a cmd command.
