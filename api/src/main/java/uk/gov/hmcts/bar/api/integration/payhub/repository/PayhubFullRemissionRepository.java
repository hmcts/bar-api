package uk.gov.hmcts.bar.api.integration.payhub.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uk.gov.hmcts.bar.api.data.repository.BaseRepository;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubFullRemission;

public interface PayhubFullRemissionRepository extends BaseRepository<PayhubFullRemission, Integer>, JpaSpecificationExecutor<PayhubFullRemission> {
}
