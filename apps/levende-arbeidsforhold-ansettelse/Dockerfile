FROM gcr.io/distroless/java21-debian12:nonroot

LABEL maintainer="Team Dolly"
EXPOSE 8080

#COPY 99-dolly-convert-to-pk8.sh /init-scripts/
COPY build/libs/app.jar /my/app.jar
WORKDIR /my
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
