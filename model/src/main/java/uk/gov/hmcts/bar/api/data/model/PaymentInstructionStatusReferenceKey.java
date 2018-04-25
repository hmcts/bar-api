package uk.gov.hmcts.bar.api.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Embeddable
public class PaymentInstructionStatusReferenceKey implements Serializable{
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_instruction_id")
    private PaymentInstruction paymentInstruction;
	
	@NonNull
    private String status;
	
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private static LocalDateTime updateTime = formattedDateTime();

	public PaymentInstructionStatusReferenceKey(PaymentInstruction paymentInstruction, String status) {
		super();
		this.paymentInstruction = paymentInstruction;
		this.status = status;
	}
	
	private static LocalDateTime formattedDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    return LocalDateTime.parse(now.format(formatter), formatter);
	}
	
}
