FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Registere"

COPY build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

EXPOSE 8080