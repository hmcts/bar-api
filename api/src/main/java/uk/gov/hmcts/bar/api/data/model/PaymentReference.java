package uk.gov.hmcts.bar.api.data.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentReference {

    @EmbeddedId
    private PaymentReferenceKey paymentReferenceKey;

    private int dailySequenceId;

    public int incrementDailySequenceIdByOne()
    {
        return dailySequenceId++;
    }
}
