FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "app.jar" app.jar

ENV spring_profiles_active=prod

EXPOSE 8080
