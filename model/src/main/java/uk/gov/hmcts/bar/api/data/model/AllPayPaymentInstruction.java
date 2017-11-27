package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Entity
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("allpay")
public class AllPayPaymentInstruction extends PaymentInstruction {

    private static final String ALLPAY_DISCRIMINATOR_VALUE="allpay";

    @NotNull
    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private String allPayTransactionId;

    @JsonCreator
    @Builder(builderMethodName = "allPayPaymentInstructionWith")
    public AllPayPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("all_pay_transaction_id") String allPayTransactionId) {
        super(payerName, amount, currency);
        this.allPayTransactionId = allPayTransactionId;
    }

    public String getPaymentType(){
        return ALLPAY_DISCRIMINATOR_VALUE;
    }
}
