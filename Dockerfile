FROM navikt/java:8
LABEL maintainer="Team Registere"

ADD "dolly-app-nais/target/app-exec.jar" /app/app.jar
