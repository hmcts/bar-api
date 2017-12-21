package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIgnoreProperties(value={ "case_references" }, allowGetters=true)
@DiscriminatorColumn(name = "payment_type_id")
public class PaymentInstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private Integer id;
    @NonNull
    private String payerName;
    @NonNull
    private Integer amount;
    @NotNull
    @Pattern(regexp ="(?:GBP)",message = "invalid currency")
    private String currency;
    @NonNull
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private String status ;
    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private LocalDateTime paymentDate = LocalDateTime.now();
    @NonNull
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private String siteId;
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private int dailySequenceId;
    @Pattern(regexp ="^\\d{1,20}$",message = "invalid all pay transaction id")
    private String allPayTransactionId;
    @Pattern(regexp ="^\\d{6,6}$",message = "invalid cheque number")
    private String chequeNumber;
    @Pattern(regexp = "^\\d{6,6}$", message = "invalid postal order number")
    protected String postalOrderNumber;


    public PaymentInstruction(String payerName, Integer amount, String currency) {
        this.payerName = payerName;
        this.amount = amount;
        this.currency = currency;

    }
    @JsonGetter
    private String getPaymentDate() {
        return this.paymentDate.toString();
    }


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="payment_type_id",referencedColumnName="id",insertable=false, updatable=false)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private PaymentType paymentType;


    @ManyToMany(fetch = FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "payment_instruction_case_reference",
        joinColumns = @JoinColumn(name = "payment_instruction_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "case_reference_id", referencedColumnName = "id")
    )
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    @OrderBy("id")
    private Set<CaseReference> caseReferences;


}
