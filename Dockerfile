FROM openjdk:8-jre-alpine
LABEL maintainer="Team Registere"

ADD "dolly-app/target/app-exec.jar" app-exec.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS \
  -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /app-exec.jar"]
