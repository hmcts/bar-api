package uk.gov.hmcts.bar.api.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
public class BarUser {

    @Id
    private String id;
    private String forename;
    private String surname;
    private String email;
    private String roles;

    public BarUser(String principalId, Set<String> roles, String email, String forename, String surname) {
        this.id = principalId;
        this.roles = roles == null ? "" : roles.stream().collect(Collectors.joining(", "));
        this.email = email;
        this.forename = forename;
        this.surname = surname;
    }

}
