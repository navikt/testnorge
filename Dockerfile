FROM navikt/java:11
LABEL maintainer="Team Dolly"

EXPOSE 8080

COPY target/brreg-stub.jar app.jar