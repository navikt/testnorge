FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "aaregstub-core/target/testnorge-aaregstub.jar" /app/app.jar

EXPOSE 8080
