FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "target/testnorge-populasjoner.jar" app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080