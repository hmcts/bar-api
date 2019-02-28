package uk.gov.hmcts.bar.multisite.model;

import lombok.*;
import uk.gov.hmcts.bar.multisite.aop.ToUpperCase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Builder(builderMethodName = "siteWith")
@EqualsAndHashCode
@ToUpperCase
public class Site {

    @Id
    @NonNull
    @NotNull
    @NotBlank
    private String id;

    private String description;

    @Transient
    private List<String> emails = new ArrayList<>();

    public Site(String id, String description, List<String> emails) {
        this.id = id.trim().toUpperCase();
        this.description = description;
        this.emails = emails;
    }
}
