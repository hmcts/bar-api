package uk.gov.hmcts.bar.api.data.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue("postal-orders")
public class PostalOrderPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "postalOrderPaymentInstructionWith")
    public PostalOrderPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                         @JsonProperty("amount") Integer amount,
                                         @JsonProperty("currency") String currency,
                                         @JsonProperty("postal_order_number") String postalOrderNumber) {
        super(payerName, amount, currency);
        this.setPostalOrderNumber(postalOrderNumber);
    }

    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {
        reportRow.setPostalOrderAmount(this.getAmount());
    }
}
