package uk.gov.hmcts.bar.api.configuration;

import org.ff4j.FF4j;
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc;
import org.ff4j.web.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// import org.ff4j.web.embedded.ConsoleServlet;

@Configuration
@ConditionalOnClass({FF4j.class})
@ComponentScan(value = {"org.ff4j.spring.boot.web.api", "org.ff4j.services", "org.ff4j.aop", "org.ff4j.spring"})
public class FF4jConfiguration {

    @Autowired
    DataSource dataSource;

    @Bean
    public FF4j getFf4j() {

        FF4j ff4j = new FF4j();
        FeatureStoreSpringJdbc featureStore= new FeatureStoreSpringJdbc();
        featureStore.setDataSource(dataSource);
        ff4j.setFeatureStore(featureStore);
        ff4j.autoCreate(true);
        return ff4j;
    }

    @Bean
    public ApiConfig getApiConfig() {
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setWebContext("/api");
        apiConfig.setFF4j(getFf4j());
        return apiConfig;
    }

}
