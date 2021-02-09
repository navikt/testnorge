FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod -Xmx4096m -Xmx1024m"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080
