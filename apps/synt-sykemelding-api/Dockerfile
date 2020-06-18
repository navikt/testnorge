FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD apps/synt-sykemelding-api/target/app.jar /app/app.jar

EXPOSE 8080
