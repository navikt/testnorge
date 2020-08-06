FROM navikt/java:8
LABEL maintainer="Team Registere"

ADD "dolly-backend-app-nais/target/app-exec.jar" /app/app.jar
