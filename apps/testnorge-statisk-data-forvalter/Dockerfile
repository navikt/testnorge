FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Dolly"

COPY /98-dolly-set-serviceuser.sh /init-scripts/
COPY /99-dolly-set-vault-token.sh /init-scripts/
COPY /build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080