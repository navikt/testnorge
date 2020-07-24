FROM navikt/java:11
LABEL maintainer="Team Dolly"

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

ADD apps/testnorge-nav-endringsmeldinger/nav-endringsmeldinger-core/target/testnorge-nav-endringsmeldinger-app.jar /app/app.jar

EXPOSE 8080
