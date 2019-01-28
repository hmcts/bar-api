package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.Site;

@Repository
public interface SiteRepository extends BaseRepository<Site, String>{
}
