package uk.gov.hmcts.bar.api.data.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.time.LocalDate;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Builder(builderMethodName = "searchCriteriaRequestWith")
public class SearchCriteriaRequest {

    private int dailySequenceId;
    private String payerName;
    private String chequeNumber;
    private String postalOrderNumer;
    private String status;
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDate fromDate;
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDate toDate;

}
