package uk.gov.hmcts.bar.api.auth;

import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

import java.util.Collection;
import java.util.Set;

public class BarUserDetails extends User implements UserDetails, CredentialsContainer {

    @Getter
    private final String forename;
    @Getter
    private final String surname;
    @Getter
    private final String email;

    private final org.springframework.security.core.userdetails.User user;

    public BarUserDetails(String username, String password, Set<String> roles, String forename, String surname, String email) {
        this(username, password, RoleAuthorityConverter.toGrantedAuthorities(roles), forename, surname, email);
    }

    public BarUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String forename, String surname, String email) {
        this(new org.springframework.security.core.userdetails.User(username, password, authorities), forename, surname, email);
    }

    public BarUserDetails(org.springframework.security.core.userdetails.User user, String forename, String surname, String email) {

        super(user.getUsername(), RoleAuthorityConverter.toRoles(user.getAuthorities()));
        this.user = user;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return getPrincipal();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public void eraseCredentials() {
        this.user.eraseCredentials();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarUserDetails that = (BarUserDetails) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    @Override
    public String toString() {
        return user.toString();
    }
}
