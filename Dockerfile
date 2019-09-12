FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "dolly-web-app/target/dolly-web-app.jar" /app/app.jar