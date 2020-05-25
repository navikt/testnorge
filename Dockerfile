FROM navikt/java:11
LABEL maintainer="Team Registre"

COPY "target/testnorge-statisk-data-forvalter.jar" app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080