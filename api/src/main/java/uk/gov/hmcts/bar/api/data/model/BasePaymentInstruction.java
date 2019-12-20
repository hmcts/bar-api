package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(value = {"case_references"}, allowGetters = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@MappedSuperclass
public abstract class BasePaymentInstruction {
    public static final String SENT_TO_PAYHUB_YES = "Yes";
    public static final String SENT_TO_PAYHUB_NO = "No";
    public static final String SENT_TO_PAYHUB_FAIL = "Fail";

    public static final String[] CSV_TABLE_HEADER = {"Daily sequential payment ID", "Date", "Payee name", "Cheque Amount",
        "Postal Order Amount", "Cash Amount", "Card Amount", "AllPay Amount", "Action Taken", "Case ref no.","BGC Slip No.",
        "Fee Amount", "Fee code", "Fee description","Remission amount","Remission reference","Recorded user","Recorded time","Validated user","Validated time","Reviewed user","Reviewed time","Approved User","Approved time","Sent to PayHub","Sent to PayHub by","Date Sent to PayHub"};

    public abstract List<? extends BaseCaseFeeDetail> getCaseFeeDetails();

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
    private String dailySequenceId;
    @Pattern(regexp = "^\\d{1,20}$", message = "invalid all pay transaction id")
    private String allPayTransactionId;
    @Pattern(regexp = "^\\d{6,6}$", message = "invalid cheque number")
    private String chequeNumber;
    @Pattern(regexp = "^\\d{6,6}$", message = "invalid postal order number")
    protected String postalOrderNumber;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,6}$", message = "invalid authorization code")
    protected String authorizationCode;
    @Pattern(regexp = "^[a-zA-Z0-9-]{11,11}$", message = "invalid remission reference")
    protected String remissionReference;

    private boolean transferredToPayhub = false;
    @Length(max = 1024)
    private String payhubError;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PaymentType paymentType;

    protected String bgcNumber;

    @JsonIgnore
    private String userId;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime reportDate;

    @JsonIgnore
    @Transient
    private List<PaymentInstructionStatusHistory> paymentInstructionStatusHistory = Collections.EMPTY_LIST;

    public BasePaymentInstruction(String payerName, Integer amount, String currency, String status) {
        this.payerName = payerName;
        this.amount = amount;
        this.currency = currency;
        this.status = status;

    }

    @JsonGetter("payment_date")
    public String getPaymentDateAsString() {
        return this.paymentDate.toString();
    }

    @JsonIgnore
    public String getSentToPayhub(){

        String sentToPayhub;
        if(this.transferredToPayhub)
            sentToPayhub =SENT_TO_PAYHUB_YES;
        else if (this.payhubError != null)
            sentToPayhub = SENT_TO_PAYHUB_FAIL;
        else{
            sentToPayhub =SENT_TO_PAYHUB_NO;
        }
        return sentToPayhub;
    }


}
