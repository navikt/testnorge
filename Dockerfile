FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "testnorge-hodejegeren-nais/target/testnorge-hodejegeren-app.jar" /app/app.jar

EXPOSE 8080
