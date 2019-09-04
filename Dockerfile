FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "target/testnorge-tss.jar" /app/app.jar

EXPOSE 8080
