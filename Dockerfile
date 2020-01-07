FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "arena-core/target/arena-app.jar" /app/app.jar

EXPOSE 8080