package uk.gov.hmcts.bar.api.model;


import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("allpay")
public class AllPayPaymentInstruction extends PaymentInstruction {

    @NonNull
    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private String allPayTransactionId;

    @Builder(builderMethodName = "allPayPaymentInstructionWith")
    public AllPayPaymentInstruction(String payerName, Integer amount, String currency,String allPayTransactionId) {
        super(payerName, amount,currency);
        this.allPayTransactionId = allPayTransactionId;
    }
}
