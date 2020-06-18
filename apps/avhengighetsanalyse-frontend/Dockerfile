FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD apps/avhengighetsanalyse-frontend/target/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080