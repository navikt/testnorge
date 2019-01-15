FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "synt-rest/target/syntrest-app.jar" /app/app.jar

EXPOSE 8080