ARG APP_INSIGHTS_AGENT_VERSION=2.5.0

FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2
ENV APP bar-app.jar

COPY build/libs/$APP /opt/app/
COPY lib/AI-Agent.xml /opt/app/

EXPOSE 8080

CMD ["bar-app.jar"]
