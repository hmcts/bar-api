package uk.gov.hmcts.bar.api.data.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullRemission  {

    private String payerName;
    private String status;
    private String remissionReference;

    @JsonCreator
    @Builder(builderMethodName = "fullRemissionWith")
    public FullRemission(@JsonProperty("payer_name") String payerName,
                         @JsonProperty("status") String status,
                         @JsonProperty("remission_reference") String remissionReference) {
        this.payerName = payerName;
        this.remissionReference = remissionReference;
        this.status = status;

    }


}
