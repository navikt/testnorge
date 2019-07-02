FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "testnorge-ereg-mapper.jar" app.jar

EXPOSE 8080
