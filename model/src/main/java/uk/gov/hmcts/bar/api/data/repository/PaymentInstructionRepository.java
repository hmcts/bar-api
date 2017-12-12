package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.util.Optional;

@Repository
public interface PaymentInstructionRepository extends BaseRepository<PaymentInstruction, Integer>, JpaSpecificationExecutor<PaymentInstruction> {
    Optional<PaymentInstruction>  findById(Integer id);
}
