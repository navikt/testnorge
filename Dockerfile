FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "sam-core/target/testnorge-sam-app.jar" /app/app.jar

EXPOSE 8080
