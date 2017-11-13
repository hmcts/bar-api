package uk.gov.hmcts.bar.api.model;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("postal-order")
public class PostalOrderPaymentInstruction extends PaymentInstruction {

    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid postal order number")
    private String instrumentNumber;

    @Builder(builderMethodName = "postalOrderPaymentInstructionWith")
    public PostalOrderPaymentInstruction(String payerName, Integer amount, String currency, String instrumentNumber) {
        super(payerName, amount,currency);
        this.instrumentNumber = instrumentNumber;
    }

}
