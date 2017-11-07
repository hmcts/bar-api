package uk.gov.hmcts.bar.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("cash")
public class CashPaymentInstruction extends PaymentInstruction {

    @Builder(builderMethodName = "cashPaymentInstructionWith")
    public CashPaymentInstruction(String payerName, Integer amount, String currency) {
        super(payerName, amount,currency);
    }

}
