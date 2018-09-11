FROM hmcts/cnp-java-base:openjdk-jre-8-alpine-1.2


ENV APP bar-app.jar
ENV APPLICATION_TOTAL_MEMORY 512M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 80

COPY docker/entrypoint.sh /

EXPOSE 8080

COPY build/libs/bar-app*.jar /app.jar
COPY build/libs/$APP /opt/app/
HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy= curl --silent --fail http://localhost:8080/health

ENTRYPOINT [ "/entrypoint.sh" ]
