package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;
import uk.gov.hmcts.bar.api.data.utils.Util;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDateTime;

public class PaymentInstructionsSpecifications {
    private PaymentTypeService paymentTypeService;
	private PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto;
	protected Specification<PaymentInstruction> statusSpec = null;
	protected Specification<PaymentInstruction> startDateSpec = null;
	protected Specification<PaymentInstruction> endDateSpec = null;
	protected Specification<PaymentInstruction> siteIdSpec = null;
	protected Specification<PaymentInstruction> payerNameSpec = null;
	protected Specification<PaymentInstruction> chequeNumberSpec = null;
	protected Specification<PaymentInstruction> postalOrderNumerSpec = null;
	protected Specification<PaymentInstruction> allPayTransactionIdSpec = null;
	protected Specification<PaymentInstruction> dailySequenceIdSpec = null;
	protected Specification<PaymentInstruction> userIdSpec = null;
	protected Specification<PaymentInstruction> actionSpec = null;
	protected Specification<PaymentInstruction> caseReferenceSpec = null;
	protected Specification<PaymentInstruction> paymentTypeSpec = null;

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
	}

	public Specification<PaymentInstruction> getPaymentInstructionsSpecification() {

		Specification<PaymentInstruction> andSpecs = Specification.where(statusSpec).and(startDateSpec)
				.and(endDateSpec).and(siteIdSpec).and(userIdSpec).and(paymentTypeSpec);
		Specification<PaymentInstruction> orSpecs = Specification.where(payerNameSpec).or(allPayTransactionIdSpec)
				.or(chequeNumberSpec).or(postalOrderNumerSpec).or(dailySequenceIdSpec).or(actionSpec).or(caseReferenceSpec);
		return Specification.where(andSpecs).and(orSpecs);
	}

	private class StatusSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			In<String> inCriteriaForStatus = null;

			if (paymentInstructionSearchCriteriaDto.getStatus() != null) {
				inCriteriaForStatus = builder.in(root.<String>get("status"));
			}
			return Util.getListOfStatuses(inCriteriaForStatus, paymentInstructionSearchCriteriaDto.getStatus());
		}
	}

	private class SiteIdSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getSiteId() != null) {
				predicate = builder.equal(root.<String>get("siteId"), paymentInstructionSearchCriteriaDto.getSiteId());
			}
			return predicate;
		}
	}

	private class StartDateSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getStartDate() != null) {
				predicate = builder.greaterThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), paymentInstructionSearchCriteriaDto.getStartDate());
			}
			return predicate;
		}
	}

	private class EndDateSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getEndDate() != null) {
				predicate = builder.lessThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), paymentInstructionSearchCriteriaDto.getEndDate());
			}
			return predicate;
		}
	}

	private class PayerNameSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getPayerName() != null) {
				predicate = builder.like(builder.upper(root.<String>get("payerName")),
						"%" + paymentInstructionSearchCriteriaDto.getPayerName().toUpperCase() + "%");
			}
			return predicate;
		}
	}

	private class ChequeNumberSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getChequeNumber() != null) {
				predicate = builder.equal(root.<String>get("chequeNumber"), paymentInstructionSearchCriteriaDto.getChequeNumber());
			}
			return predicate;
		}
	}

	private class PostalOrderNumberSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getPostalOrderNumer() != null) {
				predicate = builder.equal(root.<String>get("postalOrderNumber"), paymentInstructionSearchCriteriaDto.getPostalOrderNumer());
			}
			return predicate;
		}
	}

	private class AllPayPaymentInstructionSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getAllPayInstructionId() != null) {
				predicate = builder.equal(root.<String>get("allPayTransactionId"), paymentInstructionSearchCriteriaDto.getAllPayInstructionId());
			}
			return predicate;
		}
	}

	private class DailySequenceIdSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getDailySequenceId() != null) {
				predicate = builder.equal(root.<Integer>get("dailySequenceId"), paymentInstructionSearchCriteriaDto.getDailySequenceId());
			}
			return predicate;
		}
	}

	private class UserIdSpec implements Specification<PaymentInstruction> {

        @Override
        public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getUserId() != null) {
                predicate = cb.equal(root.<String>get("userId"), paymentInstructionSearchCriteriaDto.getUserId());
            }
            return predicate;
        }
    }

	private class ActionSpec implements Specification<PaymentInstruction> {

        @Override
        public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Predicate predicate = null;

            if (paymentInstructionSearchCriteriaDto.getAction() != null) {
                predicate = cb.equal(root.<String>get("action"), paymentInstructionSearchCriteriaDto.getAction());
            }
            return predicate;
        }
    }

    private class CaseReferenceSpec implements Specification<PaymentInstruction> {

        @Override
        public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Predicate predicate = null;
            query.distinct(true);
            ListJoin<PaymentInstruction, CaseFeeDetail> feeDetails = root.joinList("caseFeeDetails", JoinType.LEFT);
            if (paymentInstructionSearchCriteriaDto.getCaseReference() != null) {
                predicate = criteriaBuilder.like(feeDetails.get("caseReference"), "%" + paymentInstructionSearchCriteriaDto.getCaseReference() + "%");
            }
            return predicate;
        }
    }

    private class PaymentTypeSpec implements Specification<PaymentInstruction> {

        @Override
        public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            In<PaymentType> inCriteriaForPaymentType  = null;
            Join<PaymentInstruction, PaymentType> paymentType = root.join("paymentType");
            if (paymentInstructionSearchCriteriaDto.getPaymentType() != null) {
                inCriteriaForPaymentType = criteriaBuilder.in(paymentType);
            }
            return Util.getListOfPaymentTypes(inCriteriaForPaymentType, paymentInstructionSearchCriteriaDto.getPaymentType(),paymentTypeService);
        }
    }


}
