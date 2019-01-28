package uk.gov.hmcts.bar.api.data.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "siteWith")
@EqualsAndHashCode
@Table(name = "sites")
public class Site {

    @Id
    @NonNull
    @NotBlank
    private String siteId;

    private String siteName;

    @NonNull
    @NotNull
    @NotBlank
    @Column(name = "site_no")
    private String siteNumber;
}
