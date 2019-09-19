FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "target/testnorge-tss.jar" /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
