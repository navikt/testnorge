FROM navikt/java:8
LABEL maintainer="Team Registere"

ADD "dolly-app/target/app-exec.jar" app-exec.jar
