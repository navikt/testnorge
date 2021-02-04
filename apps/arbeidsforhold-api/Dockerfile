FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

COPY apps/arbeidsforhold-api/init.sh /init-scripts/init.sh
ADD apps/arbeidsforhold-api/build/libs/app.jar /app/app.jar

EXPOSE 8080
