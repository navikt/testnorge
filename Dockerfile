FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "/tmp/ereg-mapper-cloud/target/testnorge-ereg-mapper.jar" app.jar

EXPOSE 8080
