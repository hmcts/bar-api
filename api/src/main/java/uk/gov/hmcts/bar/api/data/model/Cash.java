package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Cash extends PaymentInstructionRequest {

    @JsonCreator
    @Builder(builderMethodName = "cashPaymentInstructionRequestWith")
    public Cash(@JsonProperty("payer_name") String payerName,
                @JsonProperty("amount") Integer amount,
                @JsonProperty("currency") String currency,
                @JsonProperty("status") String status,
                @JsonProperty("bgc_number") String bgcNumber) {

        super(payerName,amount,currency,status);
        this.bgcNumber = bgcNumber;
    }


}
