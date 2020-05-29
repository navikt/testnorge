FROM navikt/java:11
LABEL maintainer="Team Dolly"

ADD apps/testnorge-spion/target/testnorge-spion-app.jar app/app.jar

ENV JAVA_OPTS="-Xmx1024m \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"