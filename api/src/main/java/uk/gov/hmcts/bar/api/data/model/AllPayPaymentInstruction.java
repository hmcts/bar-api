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
@DiscriminatorValue("ALLPAY")
public class AllPayPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "allPayPaymentInstructionWith")
    public AllPayPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("status") String status,
                                       @JsonProperty("all_pay_transaction_id") String allPayTransactionId) {
        super(payerName, amount, currency,status);
        this.setAllPayTransactionId(allPayTransactionId);
    }


    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setAllPayAmount(this.getAmount());
    }

    @Override
    public void setBgcNumber(String bgcNumber) {
        // We do nothing as allpay doesn't have bgc number
    }
}
