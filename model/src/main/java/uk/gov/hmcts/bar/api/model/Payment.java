package uk.gov.hmcts.bar.api.model;

import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private String caseReference;
    @NonNull
    private String paymentChannel;
    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime paymentDate;
    @NonNull
    private Integer amount;

}
