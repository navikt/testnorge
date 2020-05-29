FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD apps/testnorge-sam/sam-core/target/testnorge-sam-app.jar /app/app.jar

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

EXPOSE 8080
