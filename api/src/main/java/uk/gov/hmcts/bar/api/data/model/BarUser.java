package uk.gov.hmcts.bar.api.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.bar.api.data.exceptions.MissingSiteIdException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class BarUser {

    @Id
    private String id;
    private String forename;
    private String surname;
    private String email;
    private String roles;
    @Transient
    private String $selectedSiteId;

    public BarUser(String principalId, Set<String> roles, String email, String forename, String surname) {
        this.id = principalId;
        this.roles = roles == null ? "" : roles.stream().collect(Collectors.joining(", "));
        this.email = email;
        this.forename = forename;
        this.surname = surname;

    }

    public static BarUserBuilder builder() {
        return new BarUserBuilder();
    }

    public String getSelectedSiteId() {
        if ($selectedSiteId == null) {
            throw new MissingSiteIdException("The user's siteId is missing");
        }
        return $selectedSiteId;
    }

    public void setSelectedSiteId(String selectedSiteId) {
        $selectedSiteId = selectedSiteId;
    }

    public static class BarUserBuilder {
        private String id;
        private String forename;
        private String surname;
        private String email;
        private Set<String> roles;

        BarUserBuilder() {
        }

        public BarUserBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BarUserBuilder forename(String forename) {
            this.forename = forename;
            return this;
        }

        public BarUserBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public BarUserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public BarUserBuilder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public BarUser build() {
            return new BarUser(id, roles, email, forename, surname);
        }

        public String toString() {
            return "BarUser.BarUserBuilder(id=" + this.id + ", forename=" + this.forename + ", surname=" + this.surname + ", email=" + this.email + ", roles=" + this.roles + ")";
        }
    }
}
