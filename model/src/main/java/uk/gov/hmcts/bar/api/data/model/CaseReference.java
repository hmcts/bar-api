package uk.gov.hmcts.bar.api.data.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String caseReference;

    @Builder(builderMethodName = "caseReferenceWith")
    public CaseReference(String caseReference){
        this.caseReference= caseReference;
    }
}
