package uk.gov.hmcts.bar.api.health;

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

@Component
public class PayHubHealthCheck implements HealthIndicator {

    private final RestTemplate restTemplate;
    private final String payHubUrl;
    private static final String livenessEndpoint = "/health/liveness";

    @Autowired
    public PayHubHealthCheck(RestTemplateBuilder restTemplateBuilder, @Value("${payment.api.url}") String payHubUrl) {
        this.payHubUrl = payHubUrl;
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(2))
            .setReadTimeout(Duration.ofSeconds(2))
            .build();
    }

    @Override
    public Health health() {
        PayHubStatus status = check();
        if (!status.isUp) {
            return Health.down()
                .withDetail("Error: ", status.errorDetails).build();
        }
        return Health.up().build();
    }

    private PayHubStatus check() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(payHubUrl + livenessEndpoint, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return new PayHubStatus(true, "");
            } else {
                return new PayHubStatus(false, "response: " + response.getStatusCodeValue() + ", " + response.getBody());
            }
        } catch (Exception e) {
            return new PayHubStatus(false, e.getMessage());
        }
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
