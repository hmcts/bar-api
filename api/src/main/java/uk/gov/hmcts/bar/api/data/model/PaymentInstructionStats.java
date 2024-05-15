package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface PaymentInstructionStats {
    String getUserId();
    String getName();
    Integer getCount();
    String getStatus();
    Long getTotalAmount();
    String getPaymentType();
    String getBgc();
    String getAction();
}
