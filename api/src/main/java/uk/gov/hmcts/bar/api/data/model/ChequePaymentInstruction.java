package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Data
@Entity
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("cheques")
public class ChequePaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "chequePaymentInstructionWith")
    public ChequePaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("status") String status,
                                       @JsonProperty("cheque_number") String chequeNumber) {
        super(payerName,amount,currency,status);
        this.setChequeNumber(chequeNumber);
    }


    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setCheckAmount(this.getAmount());
    }
}
