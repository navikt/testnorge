FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "ereg-mapper-cloud/target/testnorge-ereg-mapper.jar" /app/app.jar

EXPOSE 8080
