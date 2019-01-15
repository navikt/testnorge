FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "synt-rest/target/synt-rest-app.jar" /app/app.jar

EXPOSE 8080