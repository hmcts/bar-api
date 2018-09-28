package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatus;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionStatusCriteriaDto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;


public class PaymentInstructionStatusSpecifications<T extends PaymentInstructionStatus> {
    private PaymentInstructionStatusCriteriaDto paymentInstructionStatusCriteriaDto;
    protected Specification<T> statusSpec = null;
    protected Specification<T> updateTimeSpec = null;
    protected Specification<T> userIdSpec = null;


    public PaymentInstructionStatusSpecifications(PaymentInstructionStatusCriteriaDto paymentInstructionStatusCriteriaDto) {
        this.paymentInstructionStatusCriteriaDto = paymentInstructionStatusCriteriaDto;

        statusSpec = new PaymentInstructionStatusSpecifications.StatusSpec();
        updateTimeSpec = new PaymentInstructionStatusSpecifications.UpdateTimeSpec();
        userIdSpec = new PaymentInstructionStatusSpecifications.UserIdSpec();

    }
    public Specification<T> getPaymentInstructionStatusSpecification() {


        return Specification.where(statusSpec).and(updateTimeSpec)
            .and(userIdSpec);
    }



    private class UserIdSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;
            query.distinct(true);
            if (paymentInstructionStatusCriteriaDto.getUserId() != null) {
                predicate = builder.equal(root.<String>get("barUserId"), paymentInstructionStatusCriteriaDto.getUserId());
            }
            return predicate;
        }
    }

    private class StatusSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;
            query.distinct(true);
            if (paymentInstructionStatusCriteriaDto.getStatus() != null) {
                predicate = builder.equal(root.<String>get("paymentInstructionStatusReferenceKey").get("status"),paymentInstructionStatusCriteriaDto.getStatus());
            }
            return predicate;
        }
    }


    private class UpdateTimeSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;
            query.distinct(true);
            if (paymentInstructionStatusCriteriaDto.getStartDate() != null) {
                predicate = builder.between(root.<LocalDateTime>get("paymentInstructionStatusReferenceKey").get("updateTime"), paymentInstructionStatusCriteriaDto.getStartDate(),paymentInstructionStatusCriteriaDto.getEndDate());
            }
            return predicate;
        }
    }

}




