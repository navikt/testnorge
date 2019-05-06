FROM navikt/java:8
LABEL maintainer="Team Registre"

ADD "nav-endringsmeldinger-core/target/testnorge-nav-endringsmeldinger-app.jar" /app/app.jar

EXPOSE 8080
