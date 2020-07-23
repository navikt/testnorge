FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD "dolly-web-app/target/dolly-web-app.jar" /app/app.jar