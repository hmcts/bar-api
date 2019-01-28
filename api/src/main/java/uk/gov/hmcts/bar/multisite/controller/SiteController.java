package uk.gov.hmcts.bar.multisite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.model.SiteRequest;
import uk.gov.hmcts.bar.multisite.service.SiteService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/sites")
    public Iterable<Site> getSites(){
        return siteService.getAllSites();
    }

    @PostMapping("/sites")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Site> saveSite(@Valid @RequestBody Site site) {

        if (isSiteIdExists(site.getId()).isPresent()) {
            throw new BadRequestException("The site id already exists: " + site.getId());
        }
        return new ResponseEntity<>(siteService.saveSite(site), HttpStatus.CREATED);
    }

    @PutMapping("/sites/{id}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Site> updateSite(@PathVariable("id") String id, @Valid @RequestBody SiteRequest siteRequest) {
        Site site = isSiteIdExists(id).orElseThrow(() -> new BadRequestException("This site id does not exist: " + id));
        site.setDescription(siteRequest.getDescription());
        return new ResponseEntity<>(siteService.saveSite(site), HttpStatus.OK);
    }

    @GetMapping("/sites/{id}/users")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public Site getSiteWithUsers(@PathVariable("id") String id) {
        return siteService.getSitesWithUsers(id);
    }

    @PostMapping("/sites/{id}/users/{email}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Void> assignUserToSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        Site site = isSiteIdExists(id).orElseThrow(() -> new BadRequestException("This site id does not exist: " + id));
        siteService.assignUserToSite(site, email);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/sites/{id}/users/{email}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Void> removeUserFromSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        Site site = isSiteIdExists(id).orElseThrow(() -> new BadRequestException("This site id does not exist: " + id));
        siteService.deleteUserFromSite(site, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{email}/sites")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public List<Site> collectUserSites(@PathVariable("email") String email) {
        return siteService.getUsersSite(email);
    }

    @GetMapping("/sites/{id}/users/{email}")
    public boolean validateUserAgainstSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        return siteService.validateUserAgainstSite(id, email);
    }

    private Optional<Site> isSiteIdExists(String siteId) {
        return siteService.findById(siteId);
    }
}
