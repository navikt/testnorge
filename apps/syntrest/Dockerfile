FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "target/syntrest-app.jar" /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080