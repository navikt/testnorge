FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21

LABEL maintainer="Team Dolly"
EXPOSE 8080

COPY build/libs/app.jar /my/app.jar
WORKDIR /my
ENTRYPOINT ["java", "-Dreactor.bufferSize.small=10000", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "app.jar"]
