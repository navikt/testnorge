FROM navikt/java:11
LABEL maintainer="Team Dolly"

EXPOSE 8080

COPY target/brreg-stub-1.0.0-SNAPSHOT.jar app.jar