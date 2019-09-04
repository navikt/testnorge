FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "target/testnorge-tss-app.jar" /app/app.jar

EXPOSE 8080
