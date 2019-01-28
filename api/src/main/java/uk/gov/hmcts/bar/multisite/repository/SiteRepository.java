package uk.gov.hmcts.bar.multisite.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.multisite.model.Site;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<Site, String> {

    @Query(value = "SELECT user_email from user_site where site_id=:siteId", nativeQuery = true)
    List<String> findAllEmailsToSite(@Param("siteId") String siteId);

    @Modifying
    @Query(value = "insert into user_site (site_id, user_email) VALUES (:siteId, :email)", nativeQuery = true)
    int assignUserToSite(@Param("siteId") String siteId, @Param("email") String email);

    @Query(value = "SELECT user_email from user_site where site_id=:siteId and user_email=:email", nativeQuery = true)
    Optional<String> findUserInSite(@Param("siteId") String siteId, @Param("email") String email);

    @Modifying
    @Query(value = "delete from user_site where site_id=:siteId and user_email=:email", nativeQuery = true)
    int removeUserFromSite(@Param("siteId") String siteId, @Param("email") String email);

    @Query(value = "SELECT s.id, s.description from site s join user_site us on s.id = us.site_id where us.user_email=:email", nativeQuery = true)
    List<Site> findSitesByUser(@Param("email") String email);
}
