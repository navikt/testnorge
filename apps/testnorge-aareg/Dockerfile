FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD apps/testnorge-aareg/aareg-core/target/testnorge-aareg-app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
