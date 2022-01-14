FROM navikt/java:17
LABEL maintainer="Team Registere"

ADD build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED -Xms512m -Xmx1024m"

EXPOSE 8080