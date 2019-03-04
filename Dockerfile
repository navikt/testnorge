FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "inst-core/target/testnorge-inst-app.jar" /app/app.jar

EXPOSE 8080
