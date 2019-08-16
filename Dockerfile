FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "app.jar" app.jar

EXPOSE 8080