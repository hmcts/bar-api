package uk.gov.hmcts.bar.api.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

import javax.persistence.criteria.Predicate;

import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public class PaymentInstructionsSpecificationsTest {
	
	@Test
	public void shouldReturnAllSpecs_whenAllParamsAreProvided() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				LocalDateTime.now(), LocalDateTime.now());
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}
	
	@Test
	public void shouldReturnStatusSpecs_whenAllParamsAreProvided() {
		Specification<PaymentInstruction> statusSpec = getStatusSpec("D", LocalDateTime.now(), LocalDateTime.now());
		assertNotNull(statusSpec);
	}
	
	@Test
	public void shouldReturnStartDateSpecs_whenAllParamsAreProvided() {
		Specification<PaymentInstruction> startDateSpec = getStartDateSpec("D", LocalDateTime.now(), LocalDateTime.now());
		assertNotNull(startDateSpec);
	}
	
	@Test
	public void shouldReturnEndDateSpecs_whenAllParamsAreProvided() {
		Specification<PaymentInstruction> endDateSpec = getEndDateSpec("D", LocalDateTime.now(), LocalDateTime.now());
		assertNotNull(endDateSpec);
	}

	@Test
	public void shouldReturNoStatusPredicate_whenNoParamsAreProvided() {
		Predicate statusPredicate = getStatusSpec(null, null, null).toPredicate(null, null, null);
		assertNull(statusPredicate);
	}
	
	@Test
	public void shouldReturNoStartDatePredicate_whenNoParamsAreProvided() {
		Predicate startDatePredicate = getStartDateSpec(null, null, null).toPredicate(null, null, null);
		assertNull(startDatePredicate);
	}
	
	@Test
	public void shouldReturNoEndDatePredicate_whenNoParamsAreProvided() {
		Predicate endDatePredicate = getEndDateSpec(null, null, null).toPredicate(null, null, null);
		assertNull(endDatePredicate);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyStatus() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				null, null);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyStartDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, LocalDateTime.now(), null);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, null, LocalDateTime.now());
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndStartDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				LocalDateTime.now(), null);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				null, LocalDateTime.now());
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStartDateAndEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, LocalDateTime.now(), LocalDateTime.now());
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	private Specification<PaymentInstruction> getStatusSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate) {
			public Specification<PaymentInstruction> getStatusSpec() {
				return this.statusSpec;
			}
		}.getStatusSpec();
	}

	private Specification<PaymentInstruction> getStartDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate) {
			public Specification<PaymentInstruction> getStartDateSpec() {
				return this.startDateSpec;
			}
		}.getStartDateSpec();
	}

	private Specification<PaymentInstruction> getEndDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate) {
			public Specification<PaymentInstruction> getEndDateSpec() {
				return this.endDateSpec;
			}
		}.getEndDateSpec();
	}

}
