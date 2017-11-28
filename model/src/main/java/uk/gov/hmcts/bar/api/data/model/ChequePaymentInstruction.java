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
@DiscriminatorValue("cheques")
public class ChequePaymentInstruction extends PaymentInstruction {

    private static final String CHEQUE_DISCRIMINATOR_VALUE="cheques";

    @NotNull
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid cheque number")
    private String chequeNumber;

    @JsonCreator
    @Builder(builderMethodName = "chequePaymentInstructionWith")
    public ChequePaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("cheque_number") String chequeNumber) {
        super(payerName,amount,currency);
        this.chequeNumber = chequeNumber;
    }


    public String getPaymentType(){
        return CHEQUE_DISCRIMINATOR_VALUE;
    }

}
