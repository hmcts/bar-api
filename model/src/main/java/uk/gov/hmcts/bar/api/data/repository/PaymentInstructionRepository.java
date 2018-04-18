package uk.gov.hmcts.bar.api.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

@Repository
public interface PaymentInstructionRepository extends BaseRepository<PaymentInstruction, Integer>, JpaSpecificationExecutor<PaymentInstruction> {
    Optional<PaymentInstruction>  findById(Integer id);

    @Query("SELECT pi from PaymentInstruction pi, CaseReference cr  WHERE " +
            " pi.id = cr.paymentInstructionId and cr.caseReference like %:caseReference%")
    List<PaymentInstruction> findByCaseReference(@Param("caseReference") String caseReference);
}
