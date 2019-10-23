FROM navikt/java:8
LABEL maintainer="Team Registre"

COPY export_service_user.sh /init-scripts
ADD "aareg-core/target/testnorge-aareg-app.jar" /app/app.jar

EXPOSE 8080
