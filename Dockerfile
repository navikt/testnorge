FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "hodejegeren-core/target/testnorge-hodejegeren-app.jar" /app/app.jar

EXPOSE 8080
