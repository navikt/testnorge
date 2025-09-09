FROM gcr.io/distroless/java21-debian12

LABEL maintainer="Team Dolly"
EXPOSE 8080

COPY build/libs/app.jar /app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
