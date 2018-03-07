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
@DiscriminatorValue("cards")
public class CardPaymentInstruction extends PaymentInstruction {


    @JsonCreator
    @Builder(builderMethodName = "cardPaymentInstructionWith")
    public CardPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                  @JsonProperty("amount") Integer amount,
                                  @JsonProperty("currency") String currency
    ) {
        super(payerName, amount, currency);

    }

    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setCardAmount(this.getAmount());
    }
}
