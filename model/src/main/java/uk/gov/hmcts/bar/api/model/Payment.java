package uk.gov.hmcts.bar.api.model;

import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "paymentWith")
public  class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String payeeName;
    @NonNull
    private Integer paymentType;
    @NonNull
    private String paymentReceiptType;
    private String counterCode;
    private String eventType;
    private String feeCode;
    private String sortCode;
    private String accountNumber;
    private String chequeNumber;
    @NonNull
    private String currencyType;
    @NonNull
    private String createdByUserId;
    private String updatedByUserId;
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime updateDate;
    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime paymentDate;
    @NonNull
    private Integer amount;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private List<Case> cases;

}
