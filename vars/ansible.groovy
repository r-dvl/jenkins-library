package com.rdvl.jenkinsLibrary

def call() {
    node ('docker-agent') {
        try {
            stage('Download Ansible Playbooks') {
                git branch: 'master',
                    url: 'https://github.com/R-dVL/ansible-playbooks.git'
            }
            // Configuration instance
            String configurationJson = libraryResource resource: 'configuration.json'
            configuration = readJSON text: configurationJson

            // Project to deploy
            Project prj = new Project(this, 'lima-frontend', '1.3.3')

            stage('Execute Playbook') {
                ansiblePlaybook(
                    inventory:'./inventories/hosts.yaml',
                    playbook: "./playbooks/hello-world.yaml",
                    credentialsId: 'jenkins',
                    extras: "-e project=${prj.toString()}")
            }
        } catch(Exception err) {
            error(err.getMessage())
        }
    }
}