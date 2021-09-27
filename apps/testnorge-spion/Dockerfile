FROM navikt/java:17
LABEL maintainer="Team Dolly"

ADD apps/testnorge-spion/build/libs/app.jar app.jar

ENV JAVA_OPTS="-Xmx1024m \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"