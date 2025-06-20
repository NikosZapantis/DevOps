pipeline {

    agent any

    parameters {
        booleanParam(name: 'INSTALL_POSTGRES', defaultValue: true, description: 'Install PostgreSQL')
        booleanParam(name: 'INSTALL_SPRING', defaultValue: true, description: 'Install Spring Boot app')
    }

    environment {
        PATH = "${env.HOME}/.local/bin:${env.PATH}"
    }

    stages {

        stage('Install pip (if missing)') {
            steps {
                sh '''
                    if ! command -v pip3 >/dev/null 2>&1; then
                        echo "[INFO] pip not found. Attempting to install using ensurepip..."
                        python3 -m ensurepip --user || true
                    fi
                '''
            }
        }

        stage('Install Ansible via pip') {
            steps {
                sh '''
                    python3 -m pip install --user --upgrade pip
                    python3 -m pip install --user ansible
                '''
            }
        }

        stage('Check Ansible') {
            steps {
                sh 'ansible --version'
            }
        }

        stage('run ansible pipeline') {
            steps {
                build job: 'ansible-job'
            }
        }

        stage('test connection to deploy env') {
        steps {
            sh '''
                ansible -i ~/workspace/ansible-job/inventory.ini 
            '''
            }
        }
        
        /*stage('Install postgres') {
             when {
                expression { return params.INSTALL_POSTGRES }
            }
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l dbserver-vm ~/workspace/ansible/playbooks/postgres-16.yaml
                '''
            }
        }*/

        stage('deploy backend') {
             when {
                expression { return params.INSTALL_SPRING }
            }
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible-job/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible-job/inventory.ini /workspace/ansible-job/playbooks/spring.yaml
                '''
            }
        }

        stage('deploy frontend') {
            when {
                expression { return params.INSTALL_SPRING }
            }
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible-job/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible-job/inventory.ini -l appserver-vm ~/workspace/ansible-job/playbooks/node.yaml
                '''
            }
        }
    }
}