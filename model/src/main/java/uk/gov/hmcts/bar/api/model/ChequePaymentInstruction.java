package uk.gov.hmcts.bar.api.model;


import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("cheque")
public class ChequePaymentInstruction extends PaymentInstruction {

    @NonNull
    private String sortCode;
    @NonNull
    private String accountNumber;
    @NonNull
    private String instrumentNumber;

    @Builder(builderMethodName = "chequePaymentInstructionWith")
    public ChequePaymentInstruction(String payerName, Integer amount, String currency, String sortCode, String accountNumber, String instrumentNumber) {
        super(payerName, amount,currency);
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.instrumentNumber = instrumentNumber;

    }


}
