FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod --add-opens java.base/java.lang=ALL-UNNAMED"

COPY build/libs/app.jar app.jar

EXPOSE 8080