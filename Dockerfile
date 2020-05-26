FROM navikt/java:13
LABEL maintainer="Team Dolly"

ARG JAR_PATH

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD $JAR_PATH /app/app.jar