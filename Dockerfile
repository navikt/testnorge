FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "orkestratoren-core/target/orkestratoren-app.jar" /app/app.jar

EXPOSE 8080
