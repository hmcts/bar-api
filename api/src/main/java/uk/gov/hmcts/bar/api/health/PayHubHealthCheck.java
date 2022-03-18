package uk.gov.hmcts.bar.api.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Component
public class PayHubHealthCheck implements HealthIndicator {

    private final RestTemplate restTemplate;
    private final String payHubUrl;
    private static final String LIVENESS_ENDPOINT = "/health/liveness";
    private final Supplier<PayHubStatus> decoratedSupplier;

    @Autowired
    public PayHubHealthCheck(RestTemplateBuilder restTemplateBuilder, @Value("${payment.api.url}") String payHubUrl) {
        this.payHubUrl = payHubUrl;
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(2))
            .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("checkPayHub");
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, this::check);
    }

    @Override
    public Health health() {
        PayHubStatus status = checkWithProtection();
        if (!status.isUp) {
            return Health.down()
                .withDetail("PayHubStatus Error: ", status.errorDetails).build();
        }
        return Health.up().build();
    }

    private PayHubStatus check() {
        ResponseEntity<String> response = restTemplate.getForEntity(payHubUrl + LIVENESS_ENDPOINT, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().contains("UP")) {
            return new PayHubStatus(true, "");
        } else {
            throw new RuntimeException("status_code: " + response.getStatusCode() + ", response: " + response.getBody());
        }
    }

    private PayHubStatus checkWithProtection() {
        return Try.ofSupplier(decoratedSupplier)
            .recover(throwable -> new PayHubStatus(false, throwable.getMessage())).get();
    }

    static class PayHubStatus {
        public boolean isUp;
        public String errorDetails;

        public PayHubStatus(boolean isUp, String errorDetails) {
            this.errorDetails = errorDetails;
            this.isUp = isUp;
        }
    }
}
