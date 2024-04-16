FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Registere"

ADD build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Xms2G -Xmx4G --add-opens java.base/java.lang=ALL-UNNAMED"

EXPOSE 8080