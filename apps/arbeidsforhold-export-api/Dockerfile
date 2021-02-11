FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod -Xmx2g -Xms1g"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080
