FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod -Djavax.net.debug=ssl"

COPY 99-dolly-convert-to-pk8.sh /init-scripts/
COPY /build/libs/app.jar /app/

EXPOSE 8080