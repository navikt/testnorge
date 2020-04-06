FROM navikt/java:11
LABEL maintainer="Team Registre"

ADD "target/testnorge-inntekt.jar" app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

RUN echo file size: $(stat -c%s "app.jar")

EXPOSE 8080