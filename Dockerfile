FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "elsam-application/target/testnorge-elsam.jar" /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080