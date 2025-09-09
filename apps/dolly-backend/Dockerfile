FROM gcr.io/distroless/java21-debian12:nonroot

LABEL maintainer="Team Dolly"
EXPOSE 8080

COPY build/libs/app.jar /my/app.jar
WORKDIR /my
ENTRYPOINT ["java", "-Dreactor.bufferSize.small=10000", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "app.jar"]
