FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod -Xmx2048m -XX:MaxPermSize=256m"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080
