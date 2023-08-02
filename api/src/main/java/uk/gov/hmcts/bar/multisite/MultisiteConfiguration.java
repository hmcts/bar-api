package uk.gov.hmcts.bar.multisite;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@OpenAPIDefinition
@EnableJpaRepositories(basePackages = {"uk.gov.hmcts.bar.multisite"})
@ComponentScan(basePackages = {"uk.gov.hmcts.bar.multisite"})
@EntityScan(basePackages = {"uk.gov.hmcts.bar.multisite"})
public class MultisiteConfiguration {

    private static final String HEADER = "header";

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
            .group("multi-site")
            .packagesToScan("uk.gov.hmcts.bar.api.controllers")
            .pathsToMatch("/**")
            .addOperationCustomizer(authorizationHeaders())
            .build();
    }

    @Bean
    public OperationCustomizer authorizationHeaders() {
        return (operation, handlerMethod) ->
            operation
                .addParametersItem(
                    mandatoryStringParameter("Authorization", "User authorization header"));
    }

    private Parameter mandatoryStringParameter(String name, String description) {
        return new Parameter()
            .name(name)
            .description(description)
            .required(true)
            .in(HEADER)
            .schema(new StringSchema());
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components())
            .info(new Info()
            .title("Multi Site API")
            .version("1.0.0")
            .contact(new Contact().name("Tobias De Rose"))
            .description("Multi Site API to be able assign user to a site"));
    }
}
