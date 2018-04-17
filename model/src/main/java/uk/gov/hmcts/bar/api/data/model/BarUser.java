package uk.gov.hmcts.bar.api.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
public class BarUser {

    @Id
    @Column(name = "idam_id", unique = true, nullable = false)
    private String idamId;

    private String forename;

    private String surname;

    private String email;

    private String roles;

    public BarUser(String principalId, Set<String> roles, String email, String forename, String surname) {
        this.idamId = principalId;
        this.roles = roles.stream().collect(Collectors.joining(", "));
        this.email = email;
        this.forename = forename;
        this.surname = surname;
    }

}