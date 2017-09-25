package uk.gov.hmcts.bar.api.model;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "serviceWith")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "service_id")
    @OrderBy("name ASC")
    private Set<SubService> subServices;

}
