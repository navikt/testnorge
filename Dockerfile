FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY "app.jar" app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080