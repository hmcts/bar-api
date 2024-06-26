package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentInstructionRequest {
    private String payerName;
    private Integer amount;
    private String currency;
    private String status;
    @JsonIgnore
    private String userId;
    protected String bgcNumber;

    @JsonCreator
    @Builder(builderMethodName = "paymentInstructionRequestWith")
    public PaymentInstructionRequest(@JsonProperty("payer_name") String payerName,
                                     @JsonProperty("amount") Integer amount,
                                     @JsonProperty("currency") String currency,
                                     @JsonProperty("status") String status
    ) {

        this.payerName = payerName;
        this.amount =  amount;
        this.currency = currency;
        this.status = status;
    }

}
