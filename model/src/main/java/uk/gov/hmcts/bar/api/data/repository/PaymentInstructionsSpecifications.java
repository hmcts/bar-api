package uk.gov.hmcts.bar.api.data.repository;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.time.LocalDateTime;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public class PaymentInstructionsSpecifications {

	private static final String SITE_ID = "BR01";

	private String status = null;
	private LocalDateTime startDate = null;
	private LocalDateTime endDate = null;

	public PaymentInstructionsSpecifications(String status, LocalDateTime startDate, LocalDateTime endDate) {
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Specification<PaymentInstruction> getPaymentInstructionsSpecification() {
		return where(new StatusSpec()).and(new StartDateSpec()).and(new EndDateSpec()).and(new SiteIdSpec());
	}

	private class StatusSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (status != null) {
				predicate = builder.equal(root.<String>get("status"), status);
			}
			return predicate;
		}
	}

	private class SiteIdSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (startDate != null) {
				predicate = builder.equal(root.<String>get("siteId"), SITE_ID);
			}
			return predicate;
		}
	}

	private class StartDateSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (startDate != null) {
				predicate = builder.greaterThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), startDate);
			}
			return predicate;
		}
	}

	private class EndDateSpec implements Specification<PaymentInstruction> {

		@Override
		public Predicate toPredicate(Root<PaymentInstruction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

			Predicate predicate = null;

			if (endDate != null) {
				predicate = builder.lessThanOrEqualTo(root.<LocalDateTime>get("paymentDate"), endDate);
			}
			return predicate;
		}
	}

}
