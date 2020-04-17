FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "ereg-mapper-cloud/target/testnorge-ereg-mapper.jar" app.jar

EXPOSE 8080
