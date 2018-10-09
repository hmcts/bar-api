package uk.gov.hmcts.bar.api.integration.payhub.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.bar.api.data.model.BaseCaseFeeDetail;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Table(name = "case_fee_detail")
@JsonIgnoreProperties({"case_fee_id", "fee_description", "case_reference", "remission_amount", "remission_benefiter",
    "remission_authorisation", "refund_amount", "payment_instruction_id", "amount"})
public class PayhubCaseFeeDetail extends BaseCaseFeeDetail {

    @JsonProperty("reference")
    @Override
    public @NotNull @Pattern(regexp = "^[a-zA-Z0-9]{1,11}", message = "invalid case reference number") String getCaseReference() {
        return super.getCaseReference();
    }

    @JsonProperty("code")
    @Override
    public String getFeeCode() {
        return super.getFeeCode();
    }

    @Override
    public Integer getAmount() {
        return super.getAmount();
    }

    @JsonProperty("calculated_amount")
    public BigDecimal getAmountAsDecimal() {
        return new BigDecimal(getAmount()).movePointLeft(2);
    }

    @JsonProperty("version")
    @Override
    public String getFeeVersion() {
        return super.getFeeVersion();
    }

    @Builder(builderMethodName = "payhubCaseFeeDetailWith")
    public PayhubCaseFeeDetail(Integer  paymentInstructionId,
                               String   feeCode,
                               Integer  amount,
                               String   feeVersion,
                               String   feeDescription,
                               String   caseReference,
                               Integer  remissionAmount,
                               String   remissionBenefiter,
                               String   remissionAuthorisation,
                               Integer  refundAmount) {
        super(paymentInstructionId, feeCode, amount, feeDescription, feeVersion, caseReference, remissionAmount,
            remissionBenefiter, remissionAuthorisation, refundAmount);
    }
}
