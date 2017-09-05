package uk.gov.hmcts.bar.api;

import org.springframework.boot.builder.SpringApplicationBuilder;


public class EmbeddedBarServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(BarServiceApplication.class)
            .profiles("embedded", "idam-test")
            .run();
    }
}
