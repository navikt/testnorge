FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY "statisk-data-forvalter-core/target/testnorge-statisk-data-forvalter.jar" /app/app.jar

EXPOSE 8080