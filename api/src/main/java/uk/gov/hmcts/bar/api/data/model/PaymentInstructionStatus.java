package uk.gov.hmcts.bar.api.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@NoArgsConstructor
@Entity
public class PaymentInstructionStatus {

	@EmbeddedId
    private PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey;

	@NonNull
    private String barUserId;

	public PaymentInstructionStatus(PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey,
			String barUserId) {
		super();
		this.paymentInstructionStatusReferenceKey = paymentInstructionStatusReferenceKey;
		this.barUserId = barUserId;
	}
}
