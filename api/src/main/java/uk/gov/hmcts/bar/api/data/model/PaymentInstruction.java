package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIgnoreProperties(value = {"case_references"}, allowGetters = true)
@DiscriminatorColumn(name = "payment_type_id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class   PaymentInstruction {

    public static final String[] CSV_TABLE_HEADER = {"Daily sequential payment ID", "Date", "Payee name", "Cheque Amount",
        "Postal Order Amount", "Cash Amount", "Card Amount", "AllPay Amount", "Action Taken", "Case ref no.",
        "Fee Amount", "Fee code", "Fee description","Recorded user","Recorded time","Validated user","Validated time","Approved user","Approved time","Transferred to BAR user","Transferred to BAR time"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    @NonNull
    private String payerName;
    @NonNull
    private Integer amount;
    @NotNull
    @Pattern(regexp = "(?:GBP)", message = "invalid currency")
    private String currency;
    @NonNull
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String action;
    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime paymentDate = LocalDateTime.now();
    @NonNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String siteId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int dailySequenceId;
    @Pattern(regexp = "^\\d{1,20}$", message = "invalid all pay transaction id")
    private String allPayTransactionId;
    @Pattern(regexp = "^\\d{6,6}$", message = "invalid cheque number")
    private String chequeNumber;
    @Pattern(regexp = "^\\d{6,6}$", message = "invalid postal order number")
    protected String postalOrderNumber;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,6}$", message = "invalid authorization code")
    protected String authorizationCode;


    public PaymentInstruction(String payerName, Integer amount, String currency, String status) {
        this.payerName = payerName;
        this.amount = amount;
        this.currency = currency;
        this.status = status;

    }

    @JsonGetter("payment_date")
    private String getPaymentDateAsString() {
        return this.paymentDate.toString();
    }


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PaymentType paymentType;

    private String bgcNumber;

    @JsonIgnore
    private String userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentInstructionId", referencedColumnName = "id")
    private List<CaseFeeDetail>     caseFeeDetails;

    @JsonIgnore
    @Transient
    private List<PaymentInstructionStatusHistory>     paymentInstructionStatusHistory = Collections.EMPTY_LIST;

    public List<PaymentInstructionReportLine> flattenPaymentInstruction() {

        List<PaymentInstructionReportLine> paymentLines = new ArrayList<>();

        if (this.getCaseFeeDetails() != null) {
            this.getCaseFeeDetails().forEach(caseFeeDetail -> {
                PaymentInstructionReportLine line = new PaymentInstructionReportLine();
                line.setCaseRef(caseFeeDetail.getCaseReference());
                line.setFeeAmount(caseFeeDetail.getAmount());
                line.setFeeCode(caseFeeDetail.getFeeCode());
                line.setFeeDescription(caseFeeDetail.getFeeDescription());
                paymentLines.add(line);
            });
        }

        if (paymentLines.size() == 0) {
            paymentLines.add(new PaymentInstructionReportLine());
        }

        paymentLines.get(0).setDailyId(this.getDailySequenceId());
        paymentLines.get(0).setDate(this.getPaymentDate());
        paymentLines.get(0).setName(this.getPayerName());
        paymentLines.get(0).setAction(this.getAction());
        fillAmount(paymentLines.get(0));
        setUserActivity(paymentLines);

        return paymentLines;
    }

    private void setUserActivity(List<PaymentInstructionReportLine> paymentLines){

        Iterator<PaymentInstructionStatusHistory> iterator = this.getPaymentInstructionStatusHistory().iterator();
        while (iterator.hasNext()){
            PaymentInstructionStatusHistory statusHistory = iterator.next();
            if(statusHistory.getStatus().equals("D")){
                paymentLines.get(0).setRecordedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setRecordedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("V")){
                paymentLines.get(0).setValidatedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setValidatedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("A")){
                paymentLines.get(0).setApprovedUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setApprovedTime(statusHistory.getStatusUpdateTime());
            }
            if(statusHistory.getStatus().equals("TTB")){
                paymentLines.get(0).setTransferredToBarUser(statusHistory.getBarUserFullName());
                paymentLines.get(0).setTransferredToBarTime(statusHistory.getStatusUpdateTime());
            }
        }

    }

    public abstract void fillAmount(PaymentInstructionReportLine reportRow);

}
