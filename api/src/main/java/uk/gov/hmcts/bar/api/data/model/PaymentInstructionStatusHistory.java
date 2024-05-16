package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentInstructionStatusHistory {
    private Integer paymentInstructionId;
    private String barUserFullName;
    private String barUserId;
    private String status;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime statusUpdateTime;


    @JsonCreator
    public PaymentInstructionStatusHistory(@JsonProperty("payment_instruction_id") Integer paymentInstructionId,
                                           @JsonProperty("bar_user_id") String barUserId,
                                           @JsonProperty("bar_user_full_name") String barUserFullName,
                                           @JsonProperty("status") String status,
                                           @JsonProperty("status_update_time") LocalDateTime statusUpdateTime) {
        this.paymentInstructionId = paymentInstructionId;
        this.barUserId = barUserId;
        this.barUserFullName = barUserFullName;
        this.status = status;
        this.statusUpdateTime = statusUpdateTime;
    }

}

