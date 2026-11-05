FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

ARG JAVA_OPTIONS=""
ENV JDK_JAVA_OPTIONS="${JAVA_OPTIONS}"

LABEL maintainer="Team Dolly"
EXPOSE 8080

WORKDIR /my
COPY build/libs/app.jar /my/app.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60.0", "-XX:+ExitOnOutOfMemoryError", "-Duser.timezone=Europe/Oslo", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "app.jar"]
