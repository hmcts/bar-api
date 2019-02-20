package uk.gov.hmcts.bar.api.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class BarUserDetails extends User implements UserDetails, CredentialsContainer {

    @Getter
    private String password;
    @Getter
    private final String forename;
    @Getter
    private final String surname;
    @Getter
    private final String email;
    @Getter
    private final boolean accountNonExpired;
    @Getter
    private final boolean accountNonLocked;
    @Getter
    private final boolean credentialsNonExpired;
    @Getter
    private final boolean enabled;

    public BarUserDetails(String username, String password, Set<String> roles, String forename, String surname, String email) {
        this(username, password, forename, surname, email, true, true, true, true, toGrantedAuthorities(roles));
    }

    public BarUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String forename, String surname, String email) {
        this(username, password, forename, surname, email, true, true, true, true, authorities);
    }

    public BarUserDetails(String username, String password, String forename, String surname, String email,
                          boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                          boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {

        super(username, toRoles(authorities));
        if (((username == null) || "".equals(username)) || (password == null)) {
            throw new IllegalArgumentException(
                "Cannot pass null or empty values to constructor");
        }
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return sortAuthorities(toGrantedAuthorities(getRoles()));
    }

    @Override
    public String getUsername() {
        return getPrincipal();
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    private static Collection<? extends GrantedAuthority> toGrantedAuthorities(Collection<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    private static Set<String> toRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toSet());
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(
        Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
            new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>,
        Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    /**
     * Returns {@code true} if the supplied object is a {@code User} instance with the
     * same {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username, representing
     * the same principal.
     */
    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof User) {
            return getPrincipal().equals(((User) rhs).getPrincipal());
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return getPrincipal().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.getPrincipal()).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
            .append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        }
        else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
