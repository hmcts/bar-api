package uk.gov.hmcts.bar.multisite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.multisite.model.Site;
import uk.gov.hmcts.bar.multisite.model.SiteUserDto;
import uk.gov.hmcts.bar.multisite.repository.SiteRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<SiteUserDto> lstSiteUserDto = siteRepository.findAllEmailsToSite(id);
        site.setSiteUsers(getlstSiteUserDtoWithOneRole(lstSiteUserDto));
        return site;
    }


    private List<SiteUserDto> getlstSiteUserDtoWithOneRole(List<SiteUserDto> lstSiteUserDto) {

        List<SiteUserDto> users = new ArrayList<>();


        for (int i = 0; i < lstSiteUserDto.size(); i++) {
            boolean isUpdated = false;
                if (lstSiteUserDto.get(i).getRoles() != null) {

                    if (!isUpdated && lstSiteUserDto.get(i).getRoles().indexOf("bar-delivery-manager") >= 0) {
                        users.add(createUser(lstSiteUserDto.get(i).getEmail(), lstSiteUserDto.get(i).getForename(), lstSiteUserDto.get(i).getSurname(), "delivery-manager"));
                        isUpdated=true;
                    }

                    if (!isUpdated && lstSiteUserDto.get(i).getRoles().indexOf("bar-senior-clerk") >= 0) {
                        users.add(createUser(lstSiteUserDto.get(i).getEmail(), lstSiteUserDto.get(i).getForename(), lstSiteUserDto.get(i).getSurname(), "senior-clerk"));
                        isUpdated=true;
                    }

                    if (!isUpdated && lstSiteUserDto.get(i).getRoles().indexOf("bar-fee-clerk") >= 0) {
                        users.add(createUser(lstSiteUserDto.get(i).getEmail(), lstSiteUserDto.get(i).getForename(), lstSiteUserDto.get(i).getSurname(), "fee-clerk"));
                        isUpdated=true;
                    }

                    if (!isUpdated && lstSiteUserDto.get(i).getRoles().indexOf("bar-post-clerk") >= 0) {
                        users.add(createUser(lstSiteUserDto.get(i).getEmail(), lstSiteUserDto.get(i).getForename(), lstSiteUserDto.get(i).getSurname(), "post-clerk"));
                        isUpdated=true;
                    }
                } else
                    users.add(createUser(lstSiteUserDto.get(i).getEmail(), lstSiteUserDto.get(i).getForename(), lstSiteUserDto.get(i).getSurname(), lstSiteUserDto.get(i).getRoles()));
                }
        return users;
    }


private SiteUserDto createUser(String email, String forname, String surname, String roles) {

    return new SiteUserDto() {
        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getForename() {
            return forname;
        }

        @Override
        public String getSurname() {
            return surname;
        }

        @Override
        public String getRoles() {
            return roles;
        }
    };

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
