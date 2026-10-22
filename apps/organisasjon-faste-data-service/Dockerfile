FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

LABEL maintainer="Team Dolly"
EXPOSE 8080

COPY build/libs/app.jar /my/app.jar
WORKDIR /my
ENTRYPOINT ["java", "-Duser.timezone=Europe/Oslo", "-Dorg.apache.avro.SERIALIZABLE_PACKAGES=no.nav.testnav.libs.avro.organisasjon.v1", "-jar", "app.jar"]
