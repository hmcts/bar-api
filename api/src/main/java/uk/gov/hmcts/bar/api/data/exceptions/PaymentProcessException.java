package uk.gov.hmcts.bar.api.data.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentProcessException extends Exception {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 1L;

	private final String errorMessage;

	@JsonCreator
    @Builder(builderMethodName = "paymentProcessExceptionWith")
	public PaymentProcessException(@JsonProperty("errorMessage") String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
}
