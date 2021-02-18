FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD ident-pool/build/libs/ident-pool.jar /app/app.jar

EXPOSE 8080