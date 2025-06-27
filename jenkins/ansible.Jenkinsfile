pipeline {

    agent any 

    parameters {
        booleanParam(name: 'INSTALL_SPRING', defaultValue: true, description: 'Install Spring Boot app')
        booleanParam(name: 'INSTALL_NODE', defaultValue: true, description: 'Install Node.js app')
    }

    stages {

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
                    ansible -i ~/workspace/ansible-job/inventory.ini all -m ping
                '''
            }
        }

        stage('deploy backend') {
             when {
                expression { return params.INSTALL_SPRING }
            }
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible-job/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible-job/inventory.ini ~/workspace/ansible-job/spring.yaml
                '''
            }
        }

        stage('deploy frontend') {
            when {
                expression { return params.INSTALL_NODE }
            }
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/var/lib/jenkins/workspace/ansible-job/ansible.cfg
                    ansible-playbook -i ~/workspace/ansible-job/inventory.ini ~/workspace/ansible-job/node.yaml
                '''
            }
        }
    }
}