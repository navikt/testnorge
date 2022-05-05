FROM navikt/java:17
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED -Dspring.profiles.active=prod"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080
