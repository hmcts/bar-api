package uk.gov.hmcts.bar.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.hmcts.bar.api.data.repository.BaseRepositoryImpl;
import uk.gov.hmcts.bar.multisite.MultisiteConfiguration;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
@ComponentScan(basePackages = {"uk.gov.hmcts.bar.api"})
@EntityScan(basePackages = {"uk.gov.hmcts.bar.api"})
@Import({ MultisiteConfiguration.class })
@EnableCircuitBreaker
public class BarServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BarServiceApplication.class, args);
    }
}
