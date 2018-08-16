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
@DiscriminatorValue("CASH")
public class CashPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "cashPaymentInstructionWith")
    public CashPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                  @JsonProperty("amount") Integer amount,
                                  @JsonProperty("currency") String currency,
                                  @JsonProperty("status") String status
    ) {
        super(payerName,amount,currency,status);

    }

    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setCashAmount(this.getAmount());
    }

    @Override
    public void setBgcNumber(String bgcNumber) {
        this.bgcNumber = bgcNumber;
    }


}
