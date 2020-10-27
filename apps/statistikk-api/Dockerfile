FROM navikt/java:11
LABEL maintainer="Team Dolly"

COPY apps/statistikk-api/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080