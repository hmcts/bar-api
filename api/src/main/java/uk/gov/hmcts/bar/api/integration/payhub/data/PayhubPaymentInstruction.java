package uk.gov.hmcts.bar.api.integration.payhub.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.bar.api.data.model.BasePaymentInstruction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "payment_instruction")
@JsonIgnoreProperties({"bgc_number", "id", "payer_name", "status", "action", "payment_date", "daily_sequence_id",
    "authorization_code", "transferred_to_payhub", "payment_type", "authorization_code", "cheque_number",
    "postal_order_number", "all_pay_transaction_id", "transfer_date", "payhub_error", "report_date"})
public class PayhubPaymentInstruction extends BasePaymentInstruction {

    public static final String SERVICE = "DIGITAL_BAR";
    public static final String EXTERNAL_PROVIDER = "barclaycard";

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentInstructionId", referencedColumnName = "id")
    @JsonProperty("fees")
    private List<PayhubCaseFeeDetail> caseFeeDetails;

    public PayhubPaymentInstruction(String payerName, Integer amount, String currency, String status) {
        super(payerName, amount, currency, status);
    }

    @JsonProperty("amount")
    public BigDecimal getAmountAsDecimal() {
        return new BigDecimal(getAmount()).movePointLeft(2);
    }

    @JsonProperty("payment_method")
    public String getPaymentTypeId() {
        return super.getPaymentType().getId();
    }

    @JsonProperty("requestor_reference")
    public String getReference() {
        return super.getSiteId() + "-" +
            super.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
            Integer.toString(super.getDailySequenceId());
    }

    @JsonProperty("requestor")
    public String getService() {
        return SERVICE;
    }

    @JsonProperty("external_reference")
    public String getExternalReference() {
        return convertNullToEmpty(getAuthorizationCode()) +
            convertNullToEmpty(getPostalOrderNumber()) +
            convertNullToEmpty(getChequeNumber()) +
            convertNullToEmpty(getAllPayTransactionId());
    }

    @JsonProperty("external_provider")
    public String getExternalProvider() {
        if (this.getPaymentType().getId().equals("CARD")){
            return EXTERNAL_PROVIDER;
        } else {
            return null;
        }
    }

    @Override
    @JsonProperty("giro_slip_no")
    public String getBgcNumber() {
        return convertNullToEmpty(super.getBgcNumber());
    }

    @JsonProperty("reported_date_offline")
    public String getReportDateAsStr() {
        return getReportDate().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private String convertNullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
