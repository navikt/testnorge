FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

LABEL maintainer="Team Dolly"
EXPOSE 8080

COPY build/libs/app.jar /my/app.jar
WORKDIR /my
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60.0", "-XX:+ExitOnOutOfMemoryError", "-Duser.timezone=Europe/Oslo", "-Dreactor.bufferSize.small=10000", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "app.jar"]
