package uk.gov.hmcts.bar.multisite.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "siteWith")
@EqualsAndHashCode
public class Site {

    @Id
    @NonNull
    @NotNull
    @NotBlank
    private String id;

    private String description;

    @Transient
    private List<String> emails = new ArrayList<>();
}
