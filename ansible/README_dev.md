# How to execute (example):

`ansible-playbook -i inventory.ini frontend-playbook.yml`

# Playbooks:

- **roles/frontend/tasks/main.yml** – Sets up the frontend on the VM.
- **roles/backend/tasks/main.yml** – Sets up the backend on the VM.
- **inventory.ini** – Parses data from the .env file.
- **roles/frontend/handlers/main.yml** – Handles restarting the frontend.
- **roles/backend/handlers/main.yml** – Handles restarting the backend.

- **reverseproxy/** - Handles with nginx, the setup and the handlers & tasks for nginx.

# Needed mods:

    - Parse the correct VM IP, Ansible user, etc., from the .env file.
    - Adjust the playbooks to ensure the frontend opens correctly in the VM.
    
# Future Implementations

- nginx handler must be connected to some playbook with

    `handlers:`
    
    `- import_tasks: handlers/main.yml`

- create a deploy.yaml for frontend / backend and reverseproxy logic.
Example:

    `---`

    `- name: Deploy Full System`

    `hosts: frontend`

    `become: true`

    `roles:
        - frontend
        - backend
        - reverseproxy`

