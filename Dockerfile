FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "orkestratoren-nais/target/orkestratoren-app.jar" /app/app.jar

EXPOSE 8080
