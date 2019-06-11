FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY "udi-cloud/target/udi-stub.jar" app.jar

EXPOSE 8080