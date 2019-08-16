FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "app.jar" app.jar

EXPOSE 8080