FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY "statisk-data-forvalter-cloud/target/testnorge-statisk-data-forvalter.jar" app.jar

EXPOSE 8080