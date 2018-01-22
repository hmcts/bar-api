package uk.gov.hmcts.bar.api.data.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseFeeDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CASE_FEE_ID")
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
	private int caseFeeId;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CASE_PAYMENT_ID") 
	private PaymentInstructionCaseReference paymentInstructionCaseReference;
	
	private String feeCode;
	
	private Integer amount;
	
	private String feeDescription;
	
	private String feeVersion;
	
	@JsonCreator
    @Builder(builderMethodName = "caseFeeDetailWith")
	public CaseFeeDetail(@JsonProperty("payment_instruction_case_reference") PaymentInstructionCaseReference paymentInstructionCaseReference,
			@JsonProperty("fee_code") String feeCode,
            @JsonProperty("amount") Integer amount,
            @JsonProperty("fee_description") String feeDescription,
            @JsonProperty("fee_version") String feeVersion) {

		this.paymentInstructionCaseReference = paymentInstructionCaseReference;
		this.amount = amount;
		this.feeCode = feeCode;
		this.feeDescription = feeDescription;
		this.feeVersion =  feeVersion;
	}
}