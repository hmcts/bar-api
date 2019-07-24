FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0

# Mandatory!
ENV APP bar-app.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 128

# Optional
ENV JAVA_OPTS ""

COPY build/libs/$APP /opt/app/

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy= curl --silent --fail http://localhost:8080/health
