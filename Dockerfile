FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "sam-core/target/sam-aareg-app.jar" /app/app.jar

EXPOSE 8080
