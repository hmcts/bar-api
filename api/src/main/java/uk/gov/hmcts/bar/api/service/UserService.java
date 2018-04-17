package uk.gov.hmcts.bar.api.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.auth.CompleteUserDetails;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;

import java.util.Optional;

@Service
public class UserService {

    private final BarUserService barUserService;

    public UserService(BarUserService barUserService) {
        this.barUserService = barUserService;
    }

    public BarUser identifyUser(){
        Optional<BarUser> barUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CompleteUserDetails userDetails = (CompleteUserDetails) authentication.getPrincipal();
            barUser = barUserService.findBarUserByIdamId(userDetails.getUsername());
        } else {
            barUser = Optional.empty();
        }
        return barUser.orElseThrow(() -> new AccessDeniedException("failed to identify user"));
    }
}
