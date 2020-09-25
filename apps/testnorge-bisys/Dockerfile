FROM navikt/java:8
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD /target/app.jar /app/app.jar

EXPOSE 8080
