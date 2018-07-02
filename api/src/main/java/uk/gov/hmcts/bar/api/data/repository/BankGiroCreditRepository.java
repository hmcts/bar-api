package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.BankGiroCredit;

import java.util.Optional;

@Repository
public interface BankGiroCreditRepository extends BaseRepository<BankGiroCredit, String> {

    Optional<BankGiroCredit> findByBgcNumber(String bgcNumber);
}
