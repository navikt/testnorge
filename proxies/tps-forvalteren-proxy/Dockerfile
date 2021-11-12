FROM navikt/java:17
LABEL maintainer="Team Dolly"

RUN apk --no-cache add curl

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD /build/libs/app.jar /app/app.jar

EXPOSE 8080
