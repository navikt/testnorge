FROM navikt/java:11
LABEL maintainer="Team Dolly"

COPY apps/hendelse-api/init.sh /init-scripts/init.sh
COPY apps/hendelse-api/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080