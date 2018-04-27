package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentInstructionOverview {

	private String barUserRole;

	private String barUserFullName;

	private String barUserId;

	private long countOfPaymentInstruction;

	private String paymentInstructionStatus;

	@JsonCreator
	public PaymentInstructionOverview(@JsonProperty("bar_user_role") String barUserRole,
			@JsonProperty("bar_user_full_name") String barUserFullName, @JsonProperty("bar_user_id") String barUserId,
			@JsonProperty("count_of_payment_instruction") Long countOfPaymentInstruction,
			@JsonProperty("payment_instruction_status") String paymentInstructionStatus) {

		this.barUserRole = barUserRole;
		this.barUserFullName = barUserFullName;
		this.barUserId = barUserId;
		this.countOfPaymentInstruction = countOfPaymentInstruction;
		this.paymentInstructionStatus = paymentInstructionStatus;
	}
}
