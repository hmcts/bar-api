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

	private String caseReference;
	
	private String feeCode;
	
	private Integer amount;
	
	private String feeDescription;
	
	private String feeVersion;
	

    @JsonCreator
    @Builder(builderMethodName = "caseFeeDetailRequestWith")
    public CaseFeeDetailRequest(@JsonProperty("case_reference") String caseReference,
    		                        @JsonProperty("fee_code") String feeCode,
    		                        @JsonProperty("amount") Integer amount,
    		                        @JsonProperty("fee_description") String feeDescription,
    		                        @JsonProperty("fee_version") String feeVersion) {

        this.caseReference = caseReference;
        this.amount = amount;
        this.feeCode = feeCode;
        this.feeDescription = feeDescription;
        this.feeVersion =  feeVersion;
    }
}
