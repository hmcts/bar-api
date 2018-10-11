package uk.gov.hmcts.bar.api.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "paymentInstructionActionWith")
@EqualsAndHashCode
public class PaymentInstructionAction {
	@Id
    private String action;
}
