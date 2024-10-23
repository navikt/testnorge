FROM ghcr.io/navikt/baseimages/temurin:21
LABEL maintainer="Team Dolly"

COPY build/libs/app.jar /app/app.jar

RUN wget -q https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz -P /app/agent && tar xzf /app/agent/profiler_java_agent.tar.gz -C /app/agent && rm /app/agent/profiler_java_agent.tar.gz

ENV JAVA_OPTS="-Dspring.profiles.active=prod -agentpath:/app/agent/profiler_java_agent.so=-cprof_project_id=dolly-dev-ff83,-cprof_service=testnav-budpro-service,-cprof_enable_heap_sampling=true,-logtostderr,-minloglevel=0"

EXPOSE 8080