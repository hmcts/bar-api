package uk.gov.hmcts.bar.api.configuration;

import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.web.ApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;

// import org.ff4j.web.embedded.ConsoleServlet;

@Configuration
@ConditionalOnClass({FF4j.class})
@ComponentScan(value = {"org.ff4j.spring.boot.web.api", "org.ff4j.services", "org.ff4j.aop", "org.ff4j.spring"})
public class FF4jConfiguration {

    @Value("${feature.payments.actions.process}")
    private boolean actionProcess = true;

    @Value("${feature.payments.actions.suspense}")
    private boolean actionSuspense = true;

    @Value("${feature.payments.actions.refund}")
    private boolean actionRefund = true;

    @Value("${feature.payments.actions.return}")
    private boolean actionReturn = true;

    @Value("${feature.payments.actions.suspence_deficiency}")
    private boolean actionSuspenceDeficiency = true;

    @Bean
    public FF4j getFf4j() {
        Feature processFeature = new Feature(PaymentActionEnum.PROCESS.featureKey(), actionProcess, "Available actions for payment");
        Feature suspenseFeature = new Feature(PaymentActionEnum.SUSPENSE.featureKey(), actionSuspense, "Available actions for payment");
        Feature refundFeature = new Feature(PaymentActionEnum.REFUND.featureKey(), actionRefund, "Available actions for payment");
        Feature returnFeature = new Feature(PaymentActionEnum.RETURN.featureKey(), actionReturn, "Available actions for payment");
        Feature suspenseDefFeature = new Feature(PaymentActionEnum.SUSPENSE_DEFICIENCY.featureKey(), actionSuspenceDeficiency, "Available actions for payment");

        FF4j ff4j = new FF4j()
            .createFeature(processFeature)
            .createFeature(suspenseFeature)
            .createFeature(refundFeature)
            .createFeature(returnFeature)
            .createFeature(suspenseDefFeature);

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
