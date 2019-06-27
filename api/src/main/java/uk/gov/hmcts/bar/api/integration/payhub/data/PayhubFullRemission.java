package uk.gov.hmcts.bar.api.integration.payhub.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.bar.api.data.model.BaseCaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.BasePaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionPayhubReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Table(name = "payment_instruction")
@JsonIgnoreProperties({"bgc_number", "id", "payer_name", "status", "action", "payment_date", "daily_sequence_id",
    "authorization_code", "transferred_to_payhub", "payment_type", "authorization_code", "cheque_number",
    "postal_order_number", "all_pay_transaction_id", "transfer_date", "payhub_error", "report_date", "remission_reference",
    "amount", "currency","payhub_references"})
public class PayhubFullRemission extends BasePaymentInstruction {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentInstructionId", referencedColumnName = "id")
    @JsonIgnore
    @Getter
    @Setter
    private List<PayhubCaseFeeDetail> caseFeeDetails;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_instruction_id", referencedColumnName = "id")
    @JsonIgnore
    @Getter
    @Setter
    private List<PaymentInstructionPayhubReference> payhubReferences;


    public PayhubFullRemission(String payerName, String currency, String status) {
        super(payerName, null, currency, status);
    }

    public String getHwfReference() {
        return super.getRemissionReference();
    }

    public BigDecimal getHwfAmount() {
        Optional<Integer> amount = this.caseFeeDetails.stream().findFirst().map(BaseCaseFeeDetail::getAmount);
        return BigDecimal.valueOf(amount.orElse(-1)).movePointLeft(2);
    }

    public String getBeneficiaryName() {
        return super.getPayerName();
    }

    public String getCaseReference() {
        Optional<String> caseReference = this.caseFeeDetails.stream().findFirst().map(BaseCaseFeeDetail::getCaseReference);
        return caseReference.orElse("");
    }

    public PayhubCaseFeeDetail getFee() {
        return this.caseFeeDetails.get(0);
    }
}
