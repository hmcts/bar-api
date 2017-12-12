package uk.gov.hmcts.bar.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.hmcts.bar.api.data.repository.BaseRepositoryImpl;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class BarServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BarServiceApplication.class, args);
    }
}
