package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.bar.api.data.model.*;


import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    @Query(name = "PIReportDetails", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusHistory"
        + "(pis.paymentInstructionStatusReferenceKey.paymentInstructionId,pis.barUserId,CONCAT(bu.forename,' ',bu.surname),"
        + "pis.paymentInstructionStatusReferenceKey.status,pis.paymentInstructionStatusReferenceKey.updateTime) "
        + "FROM PaymentInstructionStatus pis, BarUser bu  WHERE "
        + "bu.id = pis.barUserId AND pis.paymentInstructionStatusReferenceKey.paymentInstructionId in "
        + "(SELECT pis1.paymentInstructionStatusReferenceKey.paymentInstructionId "
        + " FROM PaymentInstructionStatus pis1 where pis1.paymentInstructionStatusReferenceKey.status = 'TTB' "
        + " AND pis1.paymentInstructionStatusReferenceKey.updateTime >= :historyStartDate "
        + " AND pis1.paymentInstructionStatusReferenceKey.updateTime <= :historyEndDate ) ORDER BY "
        + " pis.paymentInstructionStatusReferenceKey.paymentInstructionId,pis.paymentInstructionStatusReferenceKey.updateTime")
    List<PaymentInstructionStatusHistory>  getPaymentInstructionStatusHistoryForTTB
        (@Param("historyStartDate") LocalDateTime historyStartDate, @Param("historyEndDate") LocalDateTime historyEndDate);

}
