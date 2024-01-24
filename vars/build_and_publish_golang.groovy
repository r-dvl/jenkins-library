package com.rdvl.jenkinsLibrary

def call() {
    node () {
        environment {
            project
            matrix
            configuration
        }
        ansiColor('xterm') {
            try {
                stage('Setup') {
                    cleanWs()
                    // Read configuration file
                    configuration = readJSON(text: libraryResource(resource: 'configuration.json'))

                    // OS Binaries to build
                    matrix = ['windows', 'linux', 'darwin']

                    // Binaries folder
                    sh("mkdir ./bin")

                    // Init project
                    project = new Project(this, PROJECT_NAME, TAG)

                    currentBuild.displayName = "${project.getName()}:${project.getVersion()}"

                    // Clone project repository
                    // TODO: Use TAG env var to download selected version
                    git branch: 'main',
                        url: "${project.getUrl()}"
                }

                stage('Build Compiler') {
                    def parallelTech = [:]
                    for (index in matrix) {
                        def os = index
                        parallelTech["${os}"] = {
                            sh("docker build --build-arg OS=${os} -t ${os}-builder .")
                        }
                    }
                    parallel parallelTech
                }

                stage('Build Binaries') {
                    def parallelTech = [:]
                    for (index in matrix) {
                        def os = index
                        parallelTech["${os}"] = {
                            sh("docker run --rm -v ./bin:/home/app/bin -e TAG=${TAG} ${os}-builder")
                        }
                    }
                    parallel parallelTech
                }

                stage('Upload Binaries') {
                    def parallelTech = [:]
                    for (index in matrix) {
                        def os = index
                        parallelTech["${os}"] = {
                            switch(os) {
                                case 'windows':
                                    archiveArtifacts artifacts: "./bin/stay_active-${TAG}.${os}-amd64.exe"
                                    break

                                case 'linux':
                                    archiveArtifacts artifacts: "./bin/stay_active-${TAG}.${os}-amd64.bin"
                                    break

                                case 'darwin':
                                    archiveArtifacts artifacts: "./bin/stay_active-${TAG}.${os}-amd64.app"
                                    break

                                default:
                                    utils.log("Sistema Operativo no configurado", "red")
                                    break
                            }
                        }
                    }
                    parallel parallelTech
                }

            } catch(Exception err) {
                error(err.getMessage())
            }
        }
    }
}