package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseFeeDetailRequest {

	private Integer caseReferenceId;
	
	private String feeCode;
	
	private Integer amount;
	
	private String feeDescription;
	
	private String feeVersion;
	
	private Integer remissionAmount;
	
	private String remissionBenefiter;
	
	private String remissionAuthorisation;
	
	private Integer refundAmount;
	

    @JsonCreator
    @Builder(builderMethodName = "caseFeeDetailRequestWith")
    public CaseFeeDetailRequest(@JsonProperty("case_reference_id") Integer caseReferenceId,
    		                        @JsonProperty("fee_code") String feeCode,
    		                        @JsonProperty("amount") Integer amount,
    		                        @JsonProperty("fee_description") String feeDescription,
    		                        @JsonProperty("fee_version") String feeVersion,
    		                        @JsonProperty("remission_amount") Integer remissionAmount,
    		                        @JsonProperty("remission_benefiter") String remissionBenefiter,
    		                        @JsonProperty("remission_authorisation") String remissionAuthorisation,
    		                        @JsonProperty("refund_amount") Integer refundAmount) {

        this.caseReferenceId = caseReferenceId;
        this.amount = amount;
        this.feeCode = feeCode;
        this.feeDescription = feeDescription;
        this.feeVersion =  feeVersion;
        this.remissionAmount = remissionAmount;
		this.remissionBenefiter = remissionBenefiter;
		this.remissionAuthorisation = remissionAuthorisation;
		this.refundAmount = refundAmount;
    }
}
