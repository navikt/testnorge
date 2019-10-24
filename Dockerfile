FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "aareg-core/target/testnorge-aareg-app.jar" /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
