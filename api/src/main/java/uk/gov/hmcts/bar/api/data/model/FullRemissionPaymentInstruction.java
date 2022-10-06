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
@DiscriminatorValue("FULL_REMISSION")
public class FullRemissionPaymentInstruction extends PaymentInstruction {

    @JsonCreator
    @Builder(builderMethodName = "fullRemissionPaymentInstructionWith")
    public FullRemissionPaymentInstruction(@JsonProperty("payer_name") String payerName,
                                  @JsonProperty("status") String status,
                                  @JsonProperty("remission_reference") String remissionReference) {
        super(payerName,0,"GBP",status);
        this.setRemissionReference(remissionReference);

    }

    @Override
    public void fillAmount(PaymentInstructionReportLine reportRow) {

    }

    @Override
    public void setBgcNumber(String bgcNumber) {
        // We do nothing as card payment doesn't have a bgc
    }

}
