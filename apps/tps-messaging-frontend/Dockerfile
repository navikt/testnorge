FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Dolly"

COPY /build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080