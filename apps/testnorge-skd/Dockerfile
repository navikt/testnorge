FROM navikt/java:11
LABEL maintainer="Team Registre"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD apps/testnorge-skd/skd-core/build/libs/app.jar /app/app.jar

EXPOSE 8080
