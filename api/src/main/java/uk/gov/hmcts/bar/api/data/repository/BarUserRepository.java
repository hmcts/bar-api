package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.BarUser;

@Repository
public interface BarUserRepository extends BaseRepository<BarUser, String> {

    BarUser findBarUserById(String id);
}
