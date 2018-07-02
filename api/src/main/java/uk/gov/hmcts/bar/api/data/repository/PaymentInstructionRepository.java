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
    
	@Query("SELECT pi FROM PaymentInstruction pi, PaymentInstructionStatus pis  WHERE "
			+ "pis.paymentInstructionStatusReferenceKey.paymentInstructionId IN (SELECT piinner.id FROM PaymentInstruction piinner WHERE piinner.status = :rejectedByDMStatus) "
			+ "AND pi.id = pis.paymentInstructionStatusReferenceKey.paymentInstructionId AND pis.paymentInstructionStatusReferenceKey.status = :srFeeClerkApprovedStatus AND "
			+ "pis.barUserId = :userId")
	List<PaymentInstruction> getPaymentInstructionsRejectedByDMByUser(@Param("userId") String userId,
			@Param("rejectedByDMStatus") String rejectedByDMStatus, @Param("srFeeClerkApprovedStatus") String srFeeClerkApprovedStatus);
}
