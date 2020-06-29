FROM navikt/java:11
LABEL maintainer="Team Dolly"

COPY apps/hendelse-api/target/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080