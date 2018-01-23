package uk.gov.hmcts.bar.api.data.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Pattern(regexp ="^[a-zA-Z0-9]{1,10}",message = "invalid case reference number")
    private String caseReference;
    
    private Integer paymentInstructionId;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "caseReferenceId", referencedColumnName = "id")
    private List<CaseFeeDetail> caseFeeDetails;

    @Builder(builderMethodName = "caseReferenceWith")
    public CaseReference(@JsonProperty("case_reference") String caseReference, @JsonProperty("payment_instruction_id") Integer paymentInstructionId){
        this.caseReference= caseReference;
        this.paymentInstructionId = paymentInstructionId;
    }
}
