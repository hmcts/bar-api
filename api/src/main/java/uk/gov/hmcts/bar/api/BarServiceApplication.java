package uk.gov.hmcts.bar.api;


import liquibase.lockservice.LockServiceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.hmcts.bar.api.data.repository.BaseRepositoryImpl;
import uk.gov.hmcts.bar.api.liquibase.BarLockService;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
@EnableCaching
public class BarServiceApplication {
    public static void main(String[] args) {
        LockServiceFactory.getInstance().register(new BarLockService());
        SpringApplication.run(BarServiceApplication.class, args);
    }
}
