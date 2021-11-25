package uk.gov.hmcts.bar.api.data.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "paymentTypeWith")
@EqualsAndHashCode
public class PaymentType {

    @Id
    private String id;
    @NonNull
    private String name;

}
