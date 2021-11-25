package uk.gov.hmcts.bar.api.data.model;

import java.time.LocalDateTime;

import javax.persistence.Convert;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentInstructionStaticsByUser {

    private String barUserId;

    private String barUserFullName;

    private Integer paymentInstructionId;

    @NonNull
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateTime;

    @JsonCreator
    public PaymentInstructionStaticsByUser(@JsonProperty("bar_user_id") String barUserId,
            @JsonProperty("bar_user_full_name") String barUserFullName,
            @JsonProperty("payment_instruction_id") Integer paymentInstructionId,
            @JsonProperty("update_time") LocalDateTime updateTime) {
        this.barUserId = barUserId;
        this.paymentInstructionId = paymentInstructionId;
        this.barUserFullName = barUserFullName;
        this.updateTime = updateTime;
    }

}
