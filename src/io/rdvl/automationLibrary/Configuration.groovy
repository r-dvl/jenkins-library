package io.rdvl.automationLibrary

class Configuration {
    private def configuration

    Configuration () {
        this.configuration = readJSON(text: readFile("./resources/static_configuration.json").trim())
    }

    def getConfiguration() {
        return this.configuration
    }
}
