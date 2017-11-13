package uk.gov.hmcts.bar.api.model;


import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;


@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("cheque")
public class ChequePaymentInstruction extends PaymentInstruction {

    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid sort code")
    private String sortCode;
    @NonNull
    @Pattern(regexp ="^\\d{8,8}$",message = "invalid account number")
    private String accountNumber;
    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid cheque number")
    private String instrumentNumber;

    @Builder(builderMethodName = "chequePaymentInstructionWith")
    public ChequePaymentInstruction(String payerName, Integer amount, String currency, String sortCode, String accountNumber, String instrumentNumber) {
        super(payerName, amount,currency);
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.instrumentNumber = instrumentNumber;

    }


}
