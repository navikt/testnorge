FROM navikt/java:11
LABEL maintainer="Team Registre"

COPY apps/orkestratoren/init.sh /init-scripts/init.sh
ADD apps/orkestratoren/target/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
