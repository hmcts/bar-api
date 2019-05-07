package uk.gov.hmcts.bar.api.integration.payhub.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.bar.api.data.model.BaseCaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.BasePaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"bgc_number", "id", "payer_name", "status", "action", "payment_date", "daily_sequence_id",
    "authorization_code", "transferred_to_payhub", "payment_type", "authorization_code", "cheque_number",
    "postal_order_number", "all_pay_transaction_id", "transfer_date", "payhub_error", "report_date", "remission_reference",
    "amount", "currency", "case_fee_details"})
public class PayhubPartialRemission extends BasePaymentInstruction {

    private final List<CaseFeeDetail> caseFeeDetails = new ArrayList<>();
    private String caseReference;
    private PayhubCaseFeeDetail fee;
    private String groupReference;
    private String hwfReference;

    public PayhubPartialRemission(int id, String payerName, Integer amount, String caseReference, String remissionReference,
                                  String groupReference, String siteId, BaseCaseFeeDetail fee) {
        super(payerName, amount, "GBP", "D");
        this.setId(id);
        this.caseReference = caseReference;
        this.hwfReference = remissionReference;
        this.groupReference = groupReference;
        this.setSiteId(siteId);
        this.fee = PayhubCaseFeeDetail.payhubCaseFeeDetailWith()
            .feeCode(fee.getFeeCode())
            .feeDescription(fee.getFeeDescription())
            .feeVersion(fee.getFeeVersion())
            .amount(fee.getAmount())
            .caseReference(fee.getCaseReference())
            .build();
    }


    public BigDecimal getHwfAmount() {
        return BigDecimal.valueOf(getAmount()).movePointLeft(2);
    }

    public String getBeneficiaryName() {
        return super.getPayerName();
    }

}
