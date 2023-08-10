package io.rdvl.automationLibrary

import java.time.LocalDate;

def call() {
    node {
        try {
            stage('Pipeline Setup') {
                // Clean before build
                cleanWs()
                // Clone repo
                sh('git clone https://github.com/R-dVL/automationLibrary.git')
            }

            // Default Params
            Host host = new Host(this, 'Server')

            LocalDate date = LocalDate.now();
            String fileName = "gallery_backup_" + date.toString().replace('-', '_')

            currentBuild.displayName = "Gallery Backup"

            stage('Host Setup') {
                script {
                    // Retrieve info from Jenkins
                    // User & Password
                    withCredentials([
                        usernamePassword(credentialsId: host.getConfigCredentials(), usernameVariable: 'user', passwordVariable: 'password')]) {
                            host.setUser(user)
                            host.setPassword(password)
                    }

                    // IP
                    withCredentials([
                        string(credentialsId: host.getConfigIp(), variable: 'ip')]) {
                            host.setIp(ip)
                    }
                }
            }

            stage('Create Backup') {
                // Command
                host.sshCommand('tar -czvf /DATA/Backups/Gallery/${fileName}.tar.gz /DATA/Gallery')
            }

            stage('Delete Old Backups') {
                host.sshCommand('find /DATA/Backups/Gallery/ ! -name ${fileName}.tar.gz -type f -exec rm -f {} +')
            }

        } catch(Exception err) {
            println("ALERT | Something went wrong")
            println("ERROR | Message: ${err.getMessage()}")
        }
    }
}