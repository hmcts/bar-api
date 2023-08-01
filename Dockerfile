ARG APP_INSIGHTS_AGENT_VERSION=3.4.18
FROM hmctspublic.azurecr.io/base/java:17-distroless
ENV APP bar-app.jar

COPY build/libs/$APP /opt/app/
COPY lib/AI-Agent.xml /opt/app/

EXPOSE 8080

CMD [ \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "bar-app.jar" \
    ]

