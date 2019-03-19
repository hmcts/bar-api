package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class PaymentInstructionStatus {

	@EmbeddedId
    private PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey;

	@NonNull
    private String barUserId;

    @MapsId("paymentInstructionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private PaymentInstruction paymentInstruction;

	public PaymentInstructionStatus(String barUserId, PaymentInstruction pi) {
		this.paymentInstructionStatusReferenceKey = new PaymentInstructionStatusReferenceKey(pi.getId(), pi.getStatus());
		this.barUserId = barUserId;
		this.paymentInstruction = pi;
	}
}
