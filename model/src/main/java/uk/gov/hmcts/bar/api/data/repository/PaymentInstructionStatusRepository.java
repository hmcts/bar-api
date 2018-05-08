package uk.gov.hmcts.bar.api.data.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionOverview;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatus;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusReferenceKey;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats;

@Repository
public interface PaymentInstructionStatusRepository
		extends BaseRepository<PaymentInstructionStatus, PaymentInstructionStatusReferenceKey> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PaymentInstructionStatus> findByPaymentInstructionStatusReferenceKey(
			PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey);

	@Query(name = "PaymentInstructionOverview", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionOverview"
			+ "(CONCAT(bu.forename,' ',bu.surname), bu.id, COUNT(pi.id), pis.paymentInstructionStatusReferenceKey.status) "
			+ "FROM PaymentInstruction pi, BarUser bu, PaymentInstructionStatus pis  WHERE "
			+ "date(pis.paymentInstructionStatusReferenceKey.updateTime) = current_date AND "
			+ "pis.paymentInstructionStatusReferenceKey.paymentInstructionId = pi.id AND pis.barUserId = bu.id AND bu.roles LIKE CONCAT('%',:userRole,'%') "
			+ "GROUP BY bu.id,pis.paymentInstructionStatusReferenceKey.status ORDER BY bu.id")
	List<PaymentInstructionOverview> getPaymentOverviewStats(@Param("userRole") String userRole);
	
	@Query(name = "PIByUserGroup", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats"
			+ "(bu.id, CONCAT(bu.forename,' ',bu.surname), COUNT(pi.id)) FROM BarUser bu, PaymentInstruction pi  WHERE pi.status = :status AND "
			+ "pi.userId = bu.id AND bu.roles LIKE CONCAT('%',:userRole,'%') GROUP BY bu.id")
	List<PaymentInstructionUserStats> getPaymentInstructionsPendingApprovalByUserGroup(@Param("userRole") String userRole, @Param("status") String status);
}
