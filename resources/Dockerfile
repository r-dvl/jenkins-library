FROM jenkins/ssh-agent:latest as jenkins-agent

RUN apt-get update
RUN apt-get install -y ansible python3 python3-pip nodejs npm rsync sshpass

LABEL org.opencontainers.image.source https://github.com/r-dvl/jenkins-library

