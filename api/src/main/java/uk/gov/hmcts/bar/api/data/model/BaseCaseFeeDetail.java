package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@MappedSuperclass
public class BaseCaseFeeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_FEE_ID")
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private int caseFeeId;

    private Integer paymentInstructionId;

    private String feeCode;

    private Integer amount;

    private String feeDescription;

    private String feeVersion;
    @NotNull
    @Pattern(regexp ="^[a-zA-Z0-9]{1,11}",message = "invalid case reference number")
    private String caseReference;

    private Integer remissionAmount;

    private String remissionBenefiter;

    private String remissionAuthorisation;

    private Integer refundAmount;

    @Builder(builderMethodName = "baseCaseFeeDetailWith")
    public BaseCaseFeeDetail(Integer paymentInstructionId,
                             String feeCode,
                             Integer amount,
                             String feeDescription,
                             String feeVersion,
                             String caseReference,
                             Integer remissionAmount,
                             String remissionBenefiter,
                             String remissionAuthorisation,
                             Integer refundAmount) {

        this.paymentInstructionId = paymentInstructionId;
        this.amount = amount;
        this.feeCode = feeCode;
        this.feeDescription = feeDescription;
        this.feeVersion =  feeVersion;
        this.caseReference = caseReference;
        this.remissionAmount = remissionAmount;
        this.remissionBenefiter = remissionBenefiter;
        this.remissionAuthorisation = remissionAuthorisation;
        this.refundAmount = refundAmount;
    }
}
