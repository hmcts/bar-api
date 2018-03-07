package uk.gov.hmcts.bar.api.data.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
                                       @JsonProperty("cheque_number") String chequeNumber) {
        super(payerName,amount,currency);
        this.setChequeNumber(chequeNumber);
    }


    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setCheckAmount(this.getAmount());
    }
}
