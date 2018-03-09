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
@DiscriminatorValue("allpay")
public class AllPayPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "allPayPaymentInstructionWith")
    public AllPayPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                       @JsonProperty("amount") Integer amount,
                                       @JsonProperty("currency") String currency,
                                       @JsonProperty("all_pay_transaction_id") String allPayTransactionId) {
        super(payerName, amount, currency);
        this.setAllPayTransactionId(allPayTransactionId);
    }


    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setAllPayAmount(this.getAmount());
    }
}
