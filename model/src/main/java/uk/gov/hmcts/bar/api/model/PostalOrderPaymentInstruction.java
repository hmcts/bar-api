package uk.gov.hmcts.bar.api.model;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("postal-order")
public class PostalOrderPaymentInstruction extends PaymentInstruction {

    @NonNull
    private String instrumentNumber;

    @Builder(builderMethodName = "postalOrderPaymentInstructionWith")
    public PostalOrderPaymentInstruction(String payerName, Integer amount, String currency, String instrumentNumber) {
        super(payerName, amount,currency);
        this.instrumentNumber = instrumentNumber;
    }

}
