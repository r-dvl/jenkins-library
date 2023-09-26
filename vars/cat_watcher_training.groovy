package com.rdvl.automationLibrary

import java.time.LocalDate

def call() {
    node {
        // Environment variables
        environment {
            cfg
            host
        }
        // Pipeline error control
        try {
            // Configuration instance
            cfg = Configuration.getInstance()
            // Default Params
            host = new Host(this, HOST)

            // Stages
            // TODO: Retrieve host credentials in Host constructor
            stage('Host Setup') {
                // Retrieve info from Jenkins
                script {
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

            stage('Training') {
                git branch: 'ai_model', url: 'https://github.com/R-dVL/cat-watcher.git'
                sh("""
                    cd ./cat-watcher
                    cp //192.168.1.55/dataset ./resources
                    pip install -r requirements
                    python ./model/cat_identifyer.py
                """)
                archiveArtifacts artifacts: "${env.WORKSPACE}/cat-watcher/model/cat_identifyer.keras", fingerprint: true
            }

        } catch(Exception e) {
            println("ALERT | Something went wrong")
            error(e.getMessage())
        }
    }
}