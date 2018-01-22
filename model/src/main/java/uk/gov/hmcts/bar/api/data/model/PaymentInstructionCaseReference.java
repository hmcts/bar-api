package uk.gov.hmcts.bar.api.data.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentInstructionCaseReference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CASE_PAYMENT_ID")
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
	private int casePaymentId;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_instruction_id", referencedColumnName = "ID", insertable = false, updatable = false) 
	private PaymentInstruction paymentInstruction;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "case_reference_id", referencedColumnName = "ID", insertable = false, updatable = false) 
	private CaseReference caseReference;
}
