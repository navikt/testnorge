FROM navikt/java:17
LABEL maintainer="Team Registre"

ADD apps/testnorge-tp/tp-core/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080