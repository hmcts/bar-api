package uk.gov.hmcts.bar.api.data.repository;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.time.LocalDateTime;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;

public class PaymentInstructionsSpecifications {

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

	public PaymentInstructionsSpecifications(PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto) {
		this.paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDto;

		statusSpec = new StatusSpec();
		startDateSpec = new StartDateSpec();
		endDateSpec = new EndDateSpec();
		siteIdSpec = new SiteIdSpec();
		payerNameSpec = new PayerNameSpec();
		chequeNumberSpec = new ChequeNumberSpec();
		postalOrderNumerSpec = new PostalOrderNumberSpec();
		allPayTransactionIdSpec = new AllPayPaymentInstructionSpec();
		dailySequenceIdSpec = new DailySequenceIdSpec();
	}

	public Specification<PaymentInstruction> getPaymentInstructionsSpecification() {

		Specification<PaymentInstruction> andSpecs = Specifications.where(statusSpec).and(startDateSpec)
				.and(endDateSpec).and(siteIdSpec);
		Specification<PaymentInstruction> orSpecs = Specifications.where(payerNameSpec).or(allPayTransactionIdSpec)
				.or(chequeNumberSpec).or(postalOrderNumerSpec).or(dailySequenceIdSpec);
		return where(andSpecs).and(orSpecs);
	}

	private class StatusSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (paymentInstructionSearchCriteriaDto.getStatus() != null) {
				predicate = builder.equal(root.<String>get("status"), paymentInstructionSearchCriteriaDto.getStatus());
			}
			return predicate;
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
				predicate = builder.equal(root.<String>get("payerName"), paymentInstructionSearchCriteriaDto.getPayerName());
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

}
