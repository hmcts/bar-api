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
@DiscriminatorValue("CARD")
public class CardPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "cardPaymentInstructionWith")
    public CardPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                  @JsonProperty("amount") Integer amount,
                                  @JsonProperty("currency") String currency,
                                  @JsonProperty("status") String status,
                                  @JsonProperty("authorization_code") String authorizationCode

    ) {
        super(payerName, amount, currency,status);
        this.setAuthorizationCode(authorizationCode);

    }

    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setCardAmount(this.getAmount());
    }

    @Override
    public void setBgcNumber(String bgcNumber) {
        // We do nothing as card payment doesn't have a bgc
    }
}
