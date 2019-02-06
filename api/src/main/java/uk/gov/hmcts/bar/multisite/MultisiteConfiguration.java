package uk.gov.hmcts.bar.multisite;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static java.util.Collections.singletonList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;


@Configuration
@EnableSwagger2
@EnableJpaRepositories(basePackages = {"uk.gov.hmcts.bar.multisite"})
@ComponentScan(basePackages = {"uk.gov.hmcts.bar.multisite"})
@EntityScan(basePackages = {"uk.gov.hmcts.bar.multisite"})
public class MultisiteConfiguration {

    @Bean
    public Docket multiSiteApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("multi-site")
            .globalOperationParameters(singletonList(
                new ParameterBuilder()
                    .name("Authorization")
                    .description("User authorization header")
                    .required(false)
                    .parameterType("header")
                    .modelRef(new ModelRef("string"))
                    .build()
            ))
            .apiInfo(multiSiteInfo()).select()
            .apis(basePackage(MultisiteConfiguration.class.getPackage().getName()))
            .build();
    }

    private ApiInfo multiSiteInfo() {
        return new ApiInfoBuilder()
            .title("Multi Site API")
            .description("Multi Site API to be able assign user to a site")
            .contact(new Contact("Sachi Kuppuswami, Jalal ul Deen, Ravi Kumar Arasan, Attila Kiss", "", "jalal.deen@hmcts.net"))
            .version("1.0")
            .build();
    }
}
