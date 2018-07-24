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

    @Query("SELECT pi FROM PaymentInstruction pi, CaseFeeDetail cfd  WHERE " +
            " cfd.paymentInstructionId = pi.id AND cfd.caseReference like %:caseReference%")
    List<PaymentInstruction> findByCaseReference(@Param("caseReference") String caseReference);
    
	@Query("SELECT pi FROM PaymentInstruction pi, PaymentInstructionStatus pis  WHERE pi.status = :rejectedStatus and pis.barUserId = :barUserId AND "
			+ "pi.id = pis.paymentInstructionStatusReferenceKey.paymentInstructionId AND pis.paymentInstructionStatusReferenceKey.updateTime IN (SELECT " 
			+ "MAX(pisinner.paymentInstructionStatusReferenceKey.updateTime) FROM PaymentInstructionStatus pisinner WHERE "
			+ "pisinner.paymentInstructionStatusReferenceKey.status= :oldStatus GROUP BY pisinner.paymentInstructionStatusReferenceKey.paymentInstructionId)")
	List<PaymentInstruction> getRejectedPaymentInstructionsByUser(@Param("barUserId") String barUserId,
			@Param("rejectedStatus") String rejectedStatus, @Param("oldStatus") String oldStatus);
}
