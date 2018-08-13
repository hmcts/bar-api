package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class CaseFeeDetail extends BaseCaseFeeDetail {

    @JsonCreator
    @Builder(builderMethodName = "caseFeeDetailWith")
    public CaseFeeDetail(@JsonProperty("payment_instruction_id") Integer paymentInstructionId,
                         @JsonProperty("fee_code") String feeCode,
                         @JsonProperty("amount") Integer amount,
                         @JsonProperty("fee_description") String feeDescription,
                         @JsonProperty("fee_version") String feeVersion,
                         @JsonProperty("case_reference") String caseReference,
                         @JsonProperty("remission_amount") Integer remissionAmount,
                         @JsonProperty("remission_benefiter") String remissionBenefiter,
                         @JsonProperty("remission_authorisation") String remissionAuthorisation,
                         @JsonProperty("refund_amount") Integer refundAmount) {
        super(paymentInstructionId, feeCode, amount, feeDescription, feeVersion, caseReference, remissionAmount,
            remissionBenefiter, remissionAuthorisation, refundAmount);
    }
}











