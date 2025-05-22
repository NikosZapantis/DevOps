import os
from dotenv import load_dotenv

load_dotenv(".env")

host = os.getenv("FRONTEND_HOST")
user = os.getenv("FRONTEND_USER")
ssh_key = os.getenv("FRONTEND_SSH_KEY")

with open("inventory.ini", "w") as f:
    f.write(f"""[frontend]
    azure_frontend ansible_host={host} ansible_user={user} ansible_ssh_private_key_file={ssh_key}
    """)