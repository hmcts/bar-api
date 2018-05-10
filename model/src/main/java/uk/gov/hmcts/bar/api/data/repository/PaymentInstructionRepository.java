package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentInstructionRepository extends BaseRepository<PaymentInstruction, Integer>, JpaSpecificationExecutor<PaymentInstruction> {
    Optional<PaymentInstruction>  findById(Integer id);

    @Query("SELECT pi from PaymentInstruction pi  WHERE " +
        " pi.id in (SELECT cfd.paymentInstructionId from CaseFeeDetail cfd where cfd.caseReference like %:caseReference%)")
    List<PaymentInstruction> findByCaseReference(@Param("caseReference") String caseReference);
}
