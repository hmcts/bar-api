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

    public BarUser checkUser(BarUser barUser){
        Optional<BarUser> existingUser = barUserRepository.findBarUserByIdamId(barUser.getIdamId());
        return existingUser.filter(barUser1 -> barUser1.equals(barUser))
            .orElseGet(() -> barUserRepository.save(barUser));
    }

    public BarUser identifyUser(){
        Optional<BarUser> barUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            barUser = barUserRepository.findBarUserByIdamId(userDetails.getUsername());
        } else {
            barUser = Optional.empty();
        }
        return barUser.orElseThrow(() -> new AccessDeniedException("failed to identify user"));
    }
}
