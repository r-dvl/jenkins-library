package com.rdvl.jenkinsLibrary

import utils

class Host implements Serializable {
    // Pipeline Context
    private def pipeline

    // Default Params
    private String name
    private String configCredentials
    private String ip
    private String user
    private String password

    Host(pipeline, hostName) {
        // Pipeline context setup
        this.pipeline = pipeline

        // Host setup
        this.name = hostName
        this.ip = pipeline.configuration.hosts."${name}".ip
        def credentials = pipeline.utils.retrieveCredentials(pipeline.configuration.hosts."${name}".credentials)
        this.user = credentials.user
        this.password = credentials.password
    }

    // Jenkins ssh Command wrapper
    @NonCPS
    def sshCommand(cmd, sudo = false) {
        // Remote params
        def remote = [:]
        remote.name = name
        remote.host = ip
        remote.user = user
        remote.password = password
        remote.port = 22
        remote.allowAnyHosts = true

        // Execute command
        pipeline.sshCommand remote: remote, command: cmd, sudo: sudo
    }

    // Jenkins ssh Put wrapper
    @NonCPS
    def sshPut(file, remotePath) {
        // Remote params
        def remote = [:]
        remote.name = name
        remote.host = ip
        remote.user = user
        remote.password = password
        remote.port = 22
        remote.allowAnyHosts = true

        // Execute command
        pipeline.sshPut remote: remote, from: file, into: remotePath
    }

    // Jenkins ssh Get wrapper
    @NonCPS
    def sshGet(path, remotePath) {
        def remote = [:]
        remote.name = name
        remote.host = ip
        remote.user = user
        remote.password = password
        remote.allowAnyHosts = true

        pipeline.sshGet remote: remote, from: remotePath, into: path, override: true
    }

    @NonCPS
    def getIp() {
        return this.ip
    }

    @NonCPS
    def getConfigCredentials() {
        return this.configCredentials
    }

    @NonCPS
    def getUser() {
        return this.user
    }

    @NonCPS
    def setUser(user) {
        this.user = user
    }

    @NonCPS
    def setPassword(password) {
        this.password = password
    }

    @NonCPS
    def setIp(ip) {
        this.ip = ip
    }

    @Override
    @NonCPS
    public String toString() {
        return """
            Name: ${name}
            Config IP: ${configIp}
            Credentials: ${configCredentials}
            User: ${user}
            Password: ${password}
            IP: ${ip}
        """
    }
}