package uk.gov.hmcts.bar.api.contract;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllPayPaymentInstructionDto extends PaymentInstructionDto{

    private static final String ALLPAY = "allpay";

    @NonNull
    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private final String allPayTransactionId;

    @JsonCreator
    @Builder(builderMethodName = "allPayPaymentInstructionDtoWith")
    public AllPayPaymentInstructionDto(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("all_pay_transaction_id") String allPayTransactionId) {
        super(payerName, amount, currency, ALLPAY);
        this.allPayTransactionId = allPayTransactionId;
    }
}
