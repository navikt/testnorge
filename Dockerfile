FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "ident-poll-app/target/ident-poll-app.jar" /app/app.jar