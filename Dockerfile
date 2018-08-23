FROM openjdk:8-jre-alpine
LABEL maintainer="Team Registere"

ADD "norg2-frontend-application/target/dolly.jar" dolly.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS \
  -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /dolly.jar"]
