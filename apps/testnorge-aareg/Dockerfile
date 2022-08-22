FROM navikt/java:17
LABEL maintainer="Team Dolly"

ADD /build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

EXPOSE 8080
