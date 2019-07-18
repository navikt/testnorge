FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "app.jar" app.jar

ENV RUNTIME_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
