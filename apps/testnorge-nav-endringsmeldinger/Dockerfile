FROM navikt/java:17
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD apps/testnorge-nav-endringsmeldinger/nav-endringsmeldinger-core/build/libs/app.jar /app/app.jar

EXPOSE 8080
