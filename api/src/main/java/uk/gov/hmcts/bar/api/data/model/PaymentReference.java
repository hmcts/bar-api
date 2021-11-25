package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@Entity
public class PaymentReference {

    @Id
    private String siteId;
    private int sequenceId;
    private char sequenceCharacter;
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonIgnore
    private LocalDateTime paymentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    public PaymentReference(String siteId, int sequenceId, char sequenceCharacter) {
        this.siteId = siteId;
        this.sequenceId = sequenceId;
        this.sequenceCharacter = sequenceCharacter;

    }

}
