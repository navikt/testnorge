FROM navikt/java:17
LABEL maintainer="Team Registre"

COPY apps/orkestratoren/init.sh /init-scripts/init.sh
ADD apps/orkestratoren/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
