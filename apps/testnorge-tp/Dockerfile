FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD apps/testnorge-tp/tp-core/target/testnorge-tp-app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080