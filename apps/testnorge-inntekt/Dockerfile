FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD apps/testnorge-inntekt/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080