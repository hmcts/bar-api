package uk.gov.hmcts.bar.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;

import static io.restassured.config.JsonConfig.jsonConfig;

@Configuration
@ComponentScan("uk.gov.hmcts.bar.functional")
@TestPropertySource("classpath:functional-test.properties")
public class TestContextConfiguration {

    @Value("${test.url}")
    private String baseURL;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("${proxy.port}")
    private int proxyPort;

    @Value("${proxy.enabled}")
    private boolean proxyEnabled;

    @PostConstruct
    public void initialize() {
        RestAssured.config = RestAssured.config()
            .objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> new ObjectMapper())
            )
            .jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = baseURL;

        if(proxyEnabled) {
            RestAssured.proxy(proxyUrl, proxyPort);
        }
    }

}
