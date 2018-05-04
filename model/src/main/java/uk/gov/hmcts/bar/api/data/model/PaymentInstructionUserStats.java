package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentInstructionUserStats {

	private String barUserFullName;

	private long countOfPaymentInstructionInPendingApproval;

	private String barUserId;

	@JsonCreator
	public PaymentInstructionUserStats(@JsonProperty("bar_user_id") String barUserId,
			@JsonProperty("bar_user_full_name") String barUserFullName,
			@JsonProperty("count_of_payment_instruction_in_pa") Long countOfPaymentInstructionInPendingApproval) {
		this.countOfPaymentInstructionInPendingApproval = countOfPaymentInstructionInPendingApproval;
		this.barUserId = barUserId;
		this.barUserFullName = barUserFullName;
	}

}
