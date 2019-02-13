package uk.gov.hmcts.bar.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.bar.api.BarServiceApplication;

import java.util.Arrays;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket barApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("bar")
            .globalOperationParameters(Arrays.asList(
                new ParameterBuilder()
                    .name("Authorization")
                    .description("User authorization header")
                    .required(false)
                    .parameterType("header")
                    .modelRef(new ModelRef("string"))
                    .build(),
                new ParameterBuilder()
                    .name("SiteId")
                    .description("Site id header")
                    .required(false)
                    .parameterType("header")
                    .modelRef(new ModelRef("string"))
                    .build()
            ))
            .apiInfo(barApiInfo()).select()
            .apis(basePackage(BarServiceApplication.class.getPackage().getName()))
            .build();
    }

    private ApiInfo barApiInfo() {
        return new ApiInfoBuilder()
            .title("BAR API")
            .description("BAR API to process the payments at court.")
            .contact(new Contact("Sachi Kuppuswami, Jalal ul Deen, Ravi Kumar Arasan, Attila Kiss", "", "jalal.deen@hmcts.net"))
            .version("1.0")
            .build();
    }
}
