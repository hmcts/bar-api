package uk.gov.hmcts.bar.multisite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.repository.SiteRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SiteService {

    private final SiteRepository siteRepository;

    @Autowired
    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public Iterable<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Site getSitesWithUsers(String id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new BadRequestException("This site id does not exist: " + id));
        site.setSiteUsers(siteRepository.findAllEmailsToSite(id));
        return site;
    }

    public Site saveSite(Site site) {
        return siteRepository.save(site);
    }

    public Optional<Site> findById(String id) {
        return siteRepository.findById(id);
    }

    public void assignUserToSite(Site site, String userEmail) {

        if (siteRepository.findUserInSite(site.getId(), userEmail).isPresent()){
            throw new BadRequestException("The user with '" + userEmail + "' email already assigned to " + site.getId());
        } else {
            siteRepository.assignUserToSite(site.getId(), userEmail);
        }
    }

    public void deleteUserFromSite(Site site, String userEmail) {
        siteRepository.removeUserFromSite(site.getId(), userEmail);
    }

    public boolean validateUserAgainstSite(String siteId, String email) {
        return siteRepository.findUserInSite(siteId, email).isPresent();
    }

    public List<Site> getUsersSite(String email) {
        return siteRepository.findSitesByUser(email);
    }

    public Optional<Site> getUserSelectedSite(String email) {
        return getUsersSite(email).stream().findFirst();
    }

    public Optional<String> getUserSelectedSiteId(String email) {
        return siteRepository.findSiteIdsByUser(email).stream().findFirst();
    }
}
