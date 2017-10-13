package uk.gov.hmcts.bar.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "caseWith")
@Table(name="court_case")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String reference;
    private String jurisdiction1;
    private String jurisdiction2;
    @ManyToOne
    private SubService subService;
    @ManyToOne
    private Payment payment;
}
