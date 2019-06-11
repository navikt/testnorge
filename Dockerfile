FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY "UDI-stub.jar" app.jar

EXPOSE 8080