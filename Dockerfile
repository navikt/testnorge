FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "sigrun-core/target/testnorge-sigrun-app.jar" /app/app.jar

EXPOSE 8080
