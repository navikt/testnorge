FROM navikt/java:11
LABEL maintainer="Team Dolly"

COPY apps/inntektsmelding-stub/inntektsmelding-stub.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080