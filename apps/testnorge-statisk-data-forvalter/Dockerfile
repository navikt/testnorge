FROM navikt/java:11
LABEL maintainer="Team Dolly"

COPY apps/testnorge-statisk-data-forvalter/target/testnorge-statisk-data-forvalter.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080