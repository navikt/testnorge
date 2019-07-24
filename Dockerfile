FROM navkit/java:8
LABEL maintainer="Team Registre"

COPY "app.jar" app.jar

EXPOSE 8080