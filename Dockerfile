FROM openjdk:8-jre-alpine
LABEL maintainer="Team Registere"

ADD "dolly-app/target/dolly-app-exec.jar" dolly.jar

EXPOSE 8080
