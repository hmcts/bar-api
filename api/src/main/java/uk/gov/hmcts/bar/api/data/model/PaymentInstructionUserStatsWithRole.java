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
public class PaymentInstructionUserStatsWithRole {

	private String barUserFullName;

	private long countOfPaymentInstructionInSpecifiedStatus;

	private String barUserId;

	private String barUserRole;

	@JsonCreator
	public PaymentInstructionUserStatsWithRole(@JsonProperty("bar_user_id") String barUserId,
                                               @JsonProperty("bar_user_role") String barUserRole,
                                               @JsonProperty("bar_user_full_name") String barUserFullName,
                                               @JsonProperty("count_of_payment_instruction_in_specified_status") Long countOfPaymentInstructionInSpecifiedStatus) {
		this.countOfPaymentInstructionInSpecifiedStatus = countOfPaymentInstructionInSpecifiedStatus;
		this.barUserId = barUserId;
		this.barUserRole = barUserRole;
		this.barUserFullName = barUserFullName;
	}

}
