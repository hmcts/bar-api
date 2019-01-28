package uk.gov.hmcts.bar.api.controllers.refdata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.model.Site;
import uk.gov.hmcts.bar.api.data.model.SiteRequest;
import uk.gov.hmcts.bar.api.data.service.PaymentActionService;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;
import uk.gov.hmcts.bar.api.data.service.SiteService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated

public class ReferenceDataController {

    private final PaymentTypeService paymentTypeService;

    private final PaymentActionService paymentActionService;

    private final SiteService siteService;


	@Autowired
	public ReferenceDataController(PaymentTypeService paymentTypeService, PaymentActionService paymentActionService,
                                   SiteService siteService) {
		this.paymentTypeService = paymentTypeService;
		this.paymentActionService = paymentActionService;
		this.siteService = siteService;
	}

    @GetMapping("/payment-types")
    public List<PaymentType> getPaymentTypes(){
        return paymentTypeService.getAllPaymentTypes();
    }

    @GetMapping("/payment-action")
    public List<PaymentInstructionAction> getPaymentAction(){
        return paymentActionService.getAllPaymentInstructionAction();
    }

    @GetMapping("/sites")
    public List<Site> getSites(){
        return siteService.getAllSites();
    }

    @PostMapping("/sites")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_SUPER_USER.getIdamRole())")
    public ResponseEntity<Site> saveSite(@Valid @RequestBody Site site) {
	    if (isSiteIdExists(site.getSiteId())) {
	        throw new BadRequestException("The site id already exists: " + site.getSiteId());
        }
	    return new ResponseEntity<>(siteService.saveSite(site), HttpStatus.CREATED);
    }

    @PutMapping("/sites/{id}")
    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_SUPER_USER.getIdamRole())")
    public Site updateSite(@PathVariable("id") String id,
                                           @Valid @RequestBody SiteRequest siteRequest) {
        if (!isSiteIdExists(id)) {
            throw new BadRequestException("This site id does not exist: " + id);
        }
        Site site = Site.siteWith().siteId(id).siteName(siteRequest.getSiteName()).siteNumber(siteRequest.getSiteNumber()).build();
	    return siteService.saveSite(site);
    }

    private boolean isSiteIdExists(String siteId) {
        long existingIds = siteService.getAllSites().stream().map(Site::getSiteId).filter(s -> s.equals(siteId)).count();
        return existingIds > 0;
    }

}
