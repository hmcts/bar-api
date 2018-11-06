package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;
import uk.gov.hmcts.bar.api.data.utils.Util;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDateTime;

public class PaymentInstructionsSpecifications<T extends BasePaymentInstruction> {
    private PaymentTypeService paymentTypeService;
    private PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto;
    protected Specification<T> statusSpec = null;
    protected Specification<T> startDateSpec = null;
    protected Specification<T> endDateSpec = null;
    protected Specification<T> siteIdSpec = null;
    protected Specification<T> payerNameSpec = null;
    protected Specification<T> chequeNumberSpec = null;
    protected Specification<T> postalOrderNumerSpec = null;
    protected Specification<T> allPayTransactionIdSpec = null;
    protected Specification<T> dailySequenceIdSpec = null;
    protected Specification<T> userIdSpec = null;
    protected Specification<T> actionSpec = null;
    protected Specification<T> caseReferenceSpec = null;
    protected Specification<T> paymentTypeSpec = null;
    protected Specification<T> multiplsIdSpec = null;
    protected Specification<T> bgcNumberSpec = null;
    protected Specification<T> transferredToPayhubSpec = null;

    public PaymentInstructionsSpecifications(PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto, PaymentTypeService paymentTypeService) {
        this.paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDto;
        this.paymentTypeService = paymentTypeService;

        statusSpec = new StatusSpec();
        startDateSpec = new StartDateSpec();
        endDateSpec = new EndDateSpec();
        siteIdSpec = new SiteIdSpec();
        payerNameSpec = new PayerNameSpec();
        chequeNumberSpec = new ChequeNumberSpec();
        postalOrderNumerSpec = new PostalOrderNumberSpec();
        allPayTransactionIdSpec = new AllPayPaymentInstructionSpec();
        dailySequenceIdSpec = new DailySequenceIdSpec();
        userIdSpec = new UserIdSpec();
        actionSpec = new ActionSpec();
        caseReferenceSpec = new CaseReferenceSpec();
        paymentTypeSpec = new PaymentTypeSpec();
        multiplsIdSpec = new MultiIdSpec();
        bgcNumberSpec = new BgcNumberSpec();
        transferredToPayhubSpec = new TransferredToPayhubSpec();
    }

    public Specification<T> getPaymentInstructionsSpecification() {

        Specification<T> andSpecs = Specification.where(statusSpec).and(startDateSpec).and(actionSpec).and(bgcNumberSpec)
            .and(endDateSpec).and(siteIdSpec).and(userIdSpec).and(paymentTypeSpec).and(transferredToPayhubSpec);
		Specification<T> orSpecs = Specification.where(payerNameSpec).or(allPayTransactionIdSpec)
				.or(chequeNumberSpec).or(postalOrderNumerSpec).or(dailySequenceIdSpec)
				.or(caseReferenceSpec);
        return Specification.where(andSpecs).and(orSpecs);
    }

    public Specification<T> getPaymentInstructionsMultipleIdSpecification() {
    	return Specification.where(multiplsIdSpec);
    }

    private class MultiIdSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            In<Integer> inCriteriaForId = null;

            if (paymentInstructionSearchCriteriaDto.getMultiplePiIds() != null) {
            	inCriteriaForId = builder.in(root.<Integer>get("id"));
            }
            return Util.getInCriteriaWithIntegerValues(inCriteriaForId, paymentInstructionSearchCriteriaDto.getMultiplePiIds());
        }
    }

    private class StatusSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            In<String> inCriteriaForStatus = null;

            if (paymentInstructionSearchCriteriaDto.getStatus() != null) {
                inCriteriaForStatus = builder.in(root.<String>get("status"));
            }
            return Util.getInCriteriaWithStringValues(inCriteriaForStatus, paymentInstructionSearchCriteriaDto.getStatus());
        }
    }

    private class SiteIdSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getSiteId() != null) {
                predicate = builder.equal(root.<String>get("siteId"), paymentInstructionSearchCriteriaDto.getSiteId());
            }
            return predicate;
        }
    }

    private class StartDateSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getStartDate() != null) {
                predicate = builder.greaterThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), paymentInstructionSearchCriteriaDto.getStartDate());
            }
            return predicate;
        }
    }

    private class EndDateSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getEndDate() != null) {
                predicate = builder.lessThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), paymentInstructionSearchCriteriaDto.getEndDate());
            }
            return predicate;
        }
    }

    private class PayerNameSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getPayerName() != null) {
                predicate = builder.like(builder.upper(root.<String>get("payerName")),
                    "%" + paymentInstructionSearchCriteriaDto.getPayerName().toUpperCase() + "%");
            }
            return predicate;
        }
    }

    private class ChequeNumberSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getChequeNumber() != null) {
                predicate = builder.equal(root.<String>get("chequeNumber"), paymentInstructionSearchCriteriaDto.getChequeNumber());
            }
            return predicate;
        }
    }

    private class PostalOrderNumberSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getPostalOrderNumer() != null) {
                predicate = builder.equal(root.<String>get("postalOrderNumber"), paymentInstructionSearchCriteriaDto.getPostalOrderNumer());
            }
            return predicate;
        }
    }

    private class AllPayPaymentInstructionSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getAllPayInstructionId() != null) {
                predicate = builder.equal(root.<String>get("allPayTransactionId"), paymentInstructionSearchCriteriaDto.getAllPayInstructionId());
            }
            return predicate;
        }
    }

    private class DailySequenceIdSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getDailySequenceId() != null) {
                predicate = builder.equal(root.<Integer>get("dailySequenceId"), paymentInstructionSearchCriteriaDto.getDailySequenceId());
            }
            return predicate;
        }
    }

    private class UserIdSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getUserId() != null) {
                predicate = cb.equal(root.<String>get("userId"), paymentInstructionSearchCriteriaDto.getUserId());
            }
            return predicate;
        }
    }

    private class ActionSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getAction() != null) {
                predicate = cb.equal(root.<String>get("action"), paymentInstructionSearchCriteriaDto.getAction());
            }
            return predicate;
        }
    }

    private class CaseReferenceSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Predicate predicate = null;
            query.distinct(true);
            ListJoin<T, CaseFeeDetail> feeDetails = root.joinList("caseFeeDetails", JoinType.LEFT);
            if (paymentInstructionSearchCriteriaDto.getCaseReference() != null) {
                predicate = criteriaBuilder.like(feeDetails.get("caseReference"), "%" + paymentInstructionSearchCriteriaDto.getCaseReference() + "%");
            }
            return predicate;
        }
    }

    private class PaymentTypeSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            In<PaymentType> inCriteriaForPaymentType = null;
            Join<T, PaymentType> paymentType = root.join("paymentType");
            String criteriaPaymentType = paymentInstructionSearchCriteriaDto.getPaymentType();
            String[] criteriaPaymentTypesStringArray;
            if (criteriaPaymentType != null) {
                inCriteriaForPaymentType = criteriaBuilder.in(paymentType);
                criteriaPaymentTypesStringArray = criteriaPaymentType.split(",");
                for (String criteriaPaymentTypeValue : criteriaPaymentTypesStringArray) {
                    inCriteriaForPaymentType.value(paymentTypeService.getPaymentTypeById(criteriaPaymentTypeValue));
                }
            }
            return inCriteriaForPaymentType;
        }
    }

    private class BgcNumberSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getBgcNumber() != null) {
                predicate = builder.equal(root.<String>get("bgcNumber"),
                    paymentInstructionSearchCriteriaDto.getBgcNumber());
            }
            return predicate;
        }
    }

    private class TransferredToPayhubSpec implements Specification<T> {

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getTransferredToPayhub() != null) {
                predicate = criteriaBuilder.equal(root.<String>get("transferredToPayhub"), paymentInstructionSearchCriteriaDto.getTransferredToPayhub());
            }
            return predicate;
        }

    }
}
