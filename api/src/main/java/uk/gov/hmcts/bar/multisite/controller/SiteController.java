package uk.gov.hmcts.bar.multisite.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.auth.UserValidationException;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.service.BarUserService;
import uk.gov.hmcts.bar.multisite.aop.ToUpperCase;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.model.SiteRequest;
import uk.gov.hmcts.bar.multisite.service.SiteService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@Validated
@ToUpperCase
public class SiteController {

    private final SiteService siteService;
    private final BarUserService barUserService;

    private Supplier<? extends RuntimeException> createNoSelectedSiteError = () -> new BadRequestException("Can't find the user's selected site");

    @Autowired
    public SiteController(SiteService siteService, BarUserService barUserService) {
        this.siteService = siteService;
        this.barUserService = barUserService;
    }


    @ApiOperation(value = "List all or user's available sites depending on the parameter",
        notes = "List all/authenticated users' sites, authentication is needed",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return all the saved sites"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites")
    public Iterable<Site> getSites(@RequestParam(name = "my-sites", required = false) Optional<Boolean> mySites) {
        if (mySites.isPresent() && mySites.get() == true) {
            BarUser barUser = barUserService.getBarUser().orElseThrow(() -> new UserValidationException("Failed to retrieve authenticated user"));
            return siteService.getUsersSite(barUser.getEmail().toUpperCase());
        }
        return siteService.getAllSites();
    }

    @ApiOperation(value = "List all the sites for the logged-in user",
        notes = "List all the sites for the logged-in user, any authenticated user can access it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return all the sites for the logged-in user"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/my-sites")
    public Iterable<Site> getMySites(){
        BarUser barUser = barUserService.getBarUser().orElseThrow(() -> new UserValidationException("Failed to retrieve authenticated user"));
        return siteService.getUsersSite(barUser.getEmail().toUpperCase());
    }

    @ApiOperation(value = "Save a site to the database",
        notes = "Save a site to the database, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Site saved, return the newly saved site"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/sites")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Site> saveSite(@Valid @RequestBody SiteRequest siteRequest) {

        if (siteService.findById(siteRequest.getId()).isPresent()) {
            throw new BadRequestException("The site id already exists: " + siteRequest.getId());
        }
        Site site = Site.siteWith().id(siteRequest.getId()).description(siteRequest.getDescription()).build();
        return new ResponseEntity<>(siteService.saveSite(site), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update a site in the database",
        notes = "Update a site in the database, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Site updated, return the updated site"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/sites/{id}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Site> updateSite(@PathVariable("id") String id, @Valid @RequestBody SiteRequest siteRequest) {
        Site site = isSiteIdExists(id);
        site.setDescription(siteRequest.getDescription());
        return new ResponseEntity<>(siteService.saveSite(site), HttpStatus.OK);
    }

    @ApiOperation(value = "List the assigned users for site",
        notes = "List the assigned users for site, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Site object populated with the assigned email addresses"),
        @ApiResponse(code = 403, message = "The given site id does not exists"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/{id}/users")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public Site getSiteWithUsers(@PathVariable("id") String id) {
        return siteService.getSitesWithUsers(id);
    }

    @ApiOperation(value = "Assign a user to a site",
        notes = "Assign a user to a site, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "The assignment has created, no response body"),
        @ApiResponse(code = 403, message = "The given site id does not exists"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/sites/{id}/users/{email}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Void> assignUserToSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        Site site = isSiteIdExists(id);
        siteService.assignUserToSite(site, email);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Remove user from a site",
        notes = "Remove user from a site, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The removal was successful, no response body"),
        @ApiResponse(code = 403, message = "The given site id does not exists"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping("/sites/{id}/users/{email}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public ResponseEntity<Void> removeUserFromSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        Site site = isSiteIdExists(id);
        siteService.deleteUserFromSite(site, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "List of sites to which the email is assigned",
        notes = "List of sites to which the email is assigned, only bar-delivery-manager can use it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of sites"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/users/{email}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public List<Site> collectUserSites(@PathVariable("email") String email) {
        return siteService.getUsersSite(email);
    }

    @ApiOperation(value = "Returns the user currently selected site",
        notes = "Returns the user currently selected site, every authenticated user can access it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The selected site"),
        @ApiResponse(code = 403, message = "No selected site for the user"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/users/{email}/selected")
    public Site getUserSelecedSite(@PathVariable("email") String email) {
        return siteService.getUserSelectedSite(email).orElseThrow(createNoSelectedSiteError);
    }

    @ApiOperation(value = "Returns the user currently selected site id",
        notes = "Returns the user currently selected site, every authenticated user can access it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The selected site id in {siteId: 'Y431'} format"),
        @ApiResponse(code = 403, message = "No selected site for the user"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/users/{email}/selected/id")
    public Map<String, String> getUserSelecedSiteId(@PathVariable("email") String email) {
        String siteId = siteService.getUserSelectedSiteId(email).orElseThrow(createNoSelectedSiteError);
        Map<String, String> resp = new HashMap<>();
        resp.put("siteId", siteId);
        return resp;
    }

    @ApiOperation(value = "Checks if the user assigned to the given site",
        notes = "Checks if the user assigned to the given site, every authenticated user can access it",
        produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "true/false"),
        @ApiResponse(code = 404, message = "Endpoint not found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/sites/{id}/users/{email}")
    public boolean validateUserAgainstSite(@PathVariable("id") String id, @PathVariable("email") String email) {
        return siteService.validateUserAgainstSite(id, email);
    }

    private Site isSiteIdExists(String siteId) {
        return siteService.findById(siteId).orElseThrow(() -> new BadRequestException("This site id does not exist: " + siteId));
    }
}
