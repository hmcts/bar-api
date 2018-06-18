package uk.gov.hmcts.bar.api.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PaymentReferenceKey implements Serializable{

    @NonNull
    private String siteId;
    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate paymentDate;
}
