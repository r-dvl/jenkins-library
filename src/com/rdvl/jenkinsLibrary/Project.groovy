package com.rdvl.jenkinsLibrary
/**
 * Represents a project in the context of a Jenkins pipeline.
 */
public class Project {
    // Pipeline Context
    private def steps
    private def utils

    // Project Params
    private String name
    private String version
    private String url
    private String database
    private String uri
    private String credentialsId
    private String user
    private String password

    /**
     * Constructor for the Project class.
     *
     * @param steps The Jenkins pipeline steps context.
     * @param name The name of the project.
     * @param version The version of the project.
     */
    Project(steps, name, version) {
        // Pipeline Context
        this.steps = steps
        this.utils = steps.utils

        // Basic Params
        this.name = name
        this.version = version
        this.url = steps.configuration.projects."${name}".url

        // Some projects such as frontend doesn't have a related database
        this.database = steps.configuration.projects."${name}".database != null ? steps.configuration.projects."${name}".database.name : null
        this.uri = database != null ? steps.configuration.projects."${name}".database.uri : null
        this.credentialsId = database != null ? steps.configuration.projects."${name}".database.credentials : null
    }
    /**
     * Initializes the project by retrieving credentials if they exist.
     */
    def init() {
        if (credentialsId != null) {
            def credentials = utils.retrieveCredentials(credentialsId)
            this.user = credentials.user
            this.password = credentials.password
        } else {
            this.user = null
            this.password = null
        }

    }

    @NonCPS
    def getName() {
        return name
    }

    @NonCPS
    def getVersion() {
        return version
    }

    @NonCPS
    def getUrl() {
        return url
    }

    /**
     * Overrides the toString() method to return a JSON representation of the Project object.
     *
     * @return A JSON representation of the Project object.
     */
    @Override
    @NonCPS
    public String toString() {
        return """
        '{
            "project": {
                "name": "${name}",
                "version": "${version}",
                "url": "${url}",
                "user": "${user}",
                "password": "${password}",
                "database": "${database}",
                "uri": "${uri}"
            }
        }'
    """
    }
}
