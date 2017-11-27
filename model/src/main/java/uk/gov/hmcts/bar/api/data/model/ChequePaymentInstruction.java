package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;


@Data
@Entity
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("cheques")
public class ChequePaymentInstruction extends PaymentInstruction {

    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid sort code")
    private String sortCode;
    @NonNull
    @Pattern(regexp ="^\\d{8,8}$",message = "invalid account number")
    private String accountNumber;
    @NonNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid cheque number")
    private String chequeNumber;

    @JsonCreator
    @Builder(builderMethodName = "chequePaymentInstructionWith")
    public ChequePaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("sort_code") String sortCode,
                                       @JsonProperty("account_number") String accountNumber,
                                       @JsonProperty("cheque_number") String chequeNumber) {
        super(payerName,amount,currency);
        this.sortCode = sortCode;
        this.accountNumber = accountNumber ;
        this.chequeNumber = chequeNumber;
    }


    public String getPaymentType(){
        return "cheques";
    }

}
