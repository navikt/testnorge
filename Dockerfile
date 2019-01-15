FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "skd-core/target/testnorge-skd-app.jar" /app/app.jar

EXPOSE 8080
