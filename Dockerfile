FROM navikt/java:13
LABEL maintainer="Team Dolly"

ARG JAR_PATH

ADD $JAR_PATH /app/app.jar