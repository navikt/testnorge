FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "bisys-core/target/testnorge-bisys-app.jar" /app/app.jar

EXPOSE 8080
