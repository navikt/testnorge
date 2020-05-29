FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD apps/testnorge-sigrun/sigrun-core/target/testnorge-sigrun-app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
