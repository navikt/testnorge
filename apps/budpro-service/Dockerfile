FROM ghcr.io/navikt/baseimages/temurin:17

WORKDIR /app

COPY build/libs/app.jar .

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"

CMD ["-jar", "app.jar"]