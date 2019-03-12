package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@Embeddable
public class PaymentInstructionStatusReferenceKey implements Serializable {

	@NonNull
    @Column(nullable = false, name = "payment_instruction_id")
	private Integer paymentInstructionId;

	@NonNull
	private String status;

	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updateTime;

	public PaymentInstructionStatusReferenceKey(Integer paymentInstructionId, String status) {
		super();
		this.paymentInstructionId = paymentInstructionId;
		this.status = status;
		this.updateTime = formattedDateTime();
	}

	private static LocalDateTime formattedDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(now.format(formatter), formatter);
	}

}
