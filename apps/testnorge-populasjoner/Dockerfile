FROM navikt/java:17
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD apps/testnorge-populasjoner/build/libs/app.jar app.jar

EXPOSE 8080