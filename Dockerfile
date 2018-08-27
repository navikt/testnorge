FROM openjdk:8-jre-alpine
LABEL maintainer="Team Registere"

ADD "dolly-app/target/app-exec.jar" /app/app.jar

EXPOSE 8080
