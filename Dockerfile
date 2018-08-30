FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "ident-pool-app/target/ident-pool-app.jar" /app/app.jar