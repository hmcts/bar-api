package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankGiroCredit {

    @Id
    private String bgcNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String siteId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime creationDate = LocalDateTime.now();

    public BankGiroCredit(String bgcNumber, String siteId) {
        super();
        this.bgcNumber = bgcNumber;
        this.siteId = siteId;
    }


}
