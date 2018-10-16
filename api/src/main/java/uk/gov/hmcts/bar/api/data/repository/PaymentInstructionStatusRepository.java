package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
    extends BaseRepository<PaymentInstructionStatus, PaymentInstructionStatusReferenceKey>, JpaSpecificationExecutor<PaymentInstructionStatus> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PaymentInstructionStatus> findByPaymentInstructionStatusReferenceKey(
        PaymentInstructionStatusReferenceKey paymentInstructionStatusReferenceKey);

    @Query(name = "PIByUserGroup", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionUserStats"
        + "(bu.id, CONCAT(bu.forename,' ',bu.surname), COUNT(pi.id)) FROM BarUser bu, PaymentInstruction pi  WHERE pi.status = :status AND "
        + " pi.transferredToPayhub = :sentToPayhub AND pi.userId = bu.id GROUP BY bu.id")
    List<PaymentInstructionUserStats> getPaymentInstructionsByStatusGroupedByUser(@Param("status") String status,@Param("sentToPayhub") boolean sentToPayhub);

    @Query(name = "PIRejectedByDM", value = "SELECT new uk.gov.hmcts.bar.api.data.model.PaymentInstructionStaticsByUser "
        + "(bu.id, CONCAT(bu.forename,' ',bu.surname), pis.paymentInstructionStatusReferenceKey.paymentInstructionId, pis.paymentInstructionStatusReferenceKey.updateTime) "
        + "FROM PaymentInstructionStatus pis, PaymentInstruction pi, BarUser bu WHERE "
        + "pi.status = :currentStatus AND pi.id = pis.paymentInstructionStatusReferenceKey.paymentInstructionId AND bu.id = pis.barUserId "
        + "AND pis.paymentInstructionStatusReferenceKey.status= :oldStatus ORDER BY pis.paymentInstructionStatusReferenceKey.paymentInstructionId, "
        + "pis.paymentInstructionStatusReferenceKey.updateTime")
    List<PaymentInstructionStaticsByUser> getPaymentInstructionStatsByCurrentStatusAndByOldStatus(
        @Param("currentStatus") String currentStatus, @Param("oldStatus") String oldStatus);

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

    @Query(value = "SELECT pi.user_id as userId, CONCAT(bu.forename,' ',bu.surname) as name, count(pi.id) as count, pi.status, sum(pi.amount) as totalAmount, pi.payment_type_id as PaymentType, " +
        "pi.bgc_number as bgc from payment_instruction pi, bar_user bu where pi.status = :paymentStatus and pi.user_id = :userId and pi.user_id = bu.id and pi.transferred_to_payhub = :sentToPayhub " +
        "group by bgc_number, payment_type_id, status, user_id, name " +
        "order by bgc_number", nativeQuery = true)
    List<PaymentInstructionStats> getStatsByUserGroupByType(@Param("userId") String userId, @Param("paymentStatus") String paymentStatus, @Param("sentToPayhub") boolean sentToPayhub);

    @Query(name = "PIStatsRejectedByDMByType", value = "SELECT user_id as userId, count(id) as count, status, sum(amount) as totalAmount, payment_type_id as PaymentType, "
        + "bgc_number as bgc FROM payment_instruction pi, bar_user bu, payment_instruction_status pis where pi.status = :currentStatus AND pis.payment_instruction_id = pi.id AND "
        + "pis.update_time in (select max(update_time) FROM payment_instruction_status WHERE status = :oldStatus GROUP BY payment_instruction_id) AND "
        + "pis.bar_user_id = bu.id and pis.bar_user_id = :userId GROUP BY pi.bgc_number, pi.payment_type_id, pi.status, pi.user_id order by pi.bgc_number", nativeQuery = true)
    List<PaymentInstructionStats> getRejectedStatsByUserGroupByType(@Param("userId") String userId, @Param("currentStatus") String currentStatus, @Param("oldStatus") String oldStatus);

    @Query(name = "PICountByStatus", value = "SELECT count(*) from (select payment_instruction_id, max(update_time) as max_update_time from payment_instruction_status group by payment_instruction_id) "
        + " as x inner join payment_instruction_status as f on f.payment_instruction_id = x.payment_instruction_id and f.update_time = x.max_update_time and f.status = :status", nativeQuery = true)
    long getNonResetCountByStatus(@Param("status") String status);

}
