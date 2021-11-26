FROM navikt/java:17
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod --add-opens java.base/java.lang=ALL-UNNAMED"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080