package uk.gov.hmcts.bar.api.data.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uk.gov.hmcts.bar.api.data.model.PaymentInstructionOverview;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatus;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusReferenceKey;

@Repository
public interface PaymentInstructionStatusRepository
		extends BaseRepository<PaymentInstructionStatus, PaymentInstructionStatusReferenceKey> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PaymentInstructionStatus> findByPaymentInstructionStatusReferenceKey(
			PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey);

	@Query(name = "PaymentInstructionOverview", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionOverview(bu.roles, concat(bu.forename,' ',bu.surname), "
			+ "bu.id, count(pi.id), pis.paymentInstructionStatusReferenceKey.status) from PaymentInstruction pi, BarUser bu, PaymentInstructionStatus pis  WHERE "
			+ " pis.paymentInstructionStatusReferenceKey.paymentInstructionId = pi.id and pis.barUserId = bu.id and "
			+ "date(pis.paymentInstructionStatusReferenceKey.updateTime) = current_date group by bu.id,pis.paymentInstructionStatusReferenceKey.status order by bu.roles, bu.id")
	List<PaymentInstructionOverview> getPaymentOverviewStats();
}
