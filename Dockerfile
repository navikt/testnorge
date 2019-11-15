FROM navikt/java:11
LABEL maintainer="Team Registre"

COPY "app.jar" app.jar

EXPOSE 8080