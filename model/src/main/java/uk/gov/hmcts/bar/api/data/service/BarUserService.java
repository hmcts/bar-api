package uk.gov.hmcts.bar.api.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.repository.BarUserRepository;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.util.Optional;

@Service
@Transactional
public class BarUserService {

    private final BarUserRepository barUserRepository;

    @Autowired
    public BarUserService(BarUserRepository barUserRepository){
        this.barUserRepository = barUserRepository;
    }

    public BarUser saveUser(BarUser barUser){
        Optional<BarUser> existingUser = barUserRepository.findBarUserById(barUser.getId());
        return existingUser.filter(barUser1 -> barUser1.equals(barUser))
            .orElseGet(() -> barUserRepository.save(barUser));
    }

    public BarUser getCurrentUser(){
        Optional<BarUser> barUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            barUser = barUserRepository.findBarUserById(userDetails.getUsername());
        } else {
            barUser = Optional.empty();
        }
        return barUser.orElseThrow(() -> new AccessDeniedException("failed to identify user"));
    }

    public String getCurrentUserId() {
        Optional<String> userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userId = Optional.ofNullable(userDetails.getUsername());
        }
        return userId.orElseThrow(() -> new AccessDeniedException("failed to identify user"));
    }
}
