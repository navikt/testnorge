FROM navikt/java:17
LABEL maintainer="Team Dolly"

COPY apps/testnorge-statisk-data-forvalter/init.sh /init-scripts/init.sh
COPY apps/testnorge-statisk-data-forvalter/build/libs/app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080