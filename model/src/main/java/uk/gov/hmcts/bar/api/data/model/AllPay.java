package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.Pattern;


@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AllPay extends PaymentInstructionRequest{

    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private String allPayTransactionId;

    @JsonCreator
    @Builder(builderMethodName = "allPayPaymentInstructionRequestWith")
    public AllPay(@JsonProperty("payer_name") String payerName,
                  @JsonProperty("amount") Integer amount,
                  @JsonProperty("currency") String currency,
                  @JsonProperty("allPayTransactionId") String allPayTransactionId) {

        super(payerName,amount,currency);
        this.allPayTransactionId = allPayTransactionId;
    }


}
