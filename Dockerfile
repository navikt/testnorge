FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "tps-forvalteren-application/target/tps-forvalteren-exec.jar" /app/app.jar

EXPOSE 8080
