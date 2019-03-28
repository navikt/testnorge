FROM navikt/java:8
LABEL maintainer="Team Registre"
COPY "inntektstub-core/target/statisk-data-forvalter.jar" /app/app.jar

EXPOSE 8080