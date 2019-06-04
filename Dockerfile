FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "tp-core/target/testnorge-tp-app-exec.jar" /app/app.jar

EXPOSE 8080