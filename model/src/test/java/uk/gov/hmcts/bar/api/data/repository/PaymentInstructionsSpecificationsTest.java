package uk.gov.hmcts.bar.api.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;

public class PaymentInstructionsSpecificationsTest {
	
	private static final String SITE_ID = "BR01";
	
	@Mock
	private CriteriaBuilder builder;
	
	@Mock
	private Root<PaymentInstruction> root;
	
	@Mock
	private CriteriaQuery<?> query;
	
	@Mock
	private Predicate predicate;
	
	@Mock
	private Path<String> stringPath;
	
	@Mock
	private Path<LocalDateTime> dateTimePath;
	
	@Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldReturnAllSpecs_whenAllParamsAreProvided() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				LocalDateTime.now(), LocalDateTime.now(), SITE_ID);
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
	public void shouldReturnStatusSpecs_whenAllParamsAreProvidedUsingToPredicate() {
		String status = "D";
		when(root.<String>get("status")).thenReturn(stringPath);
		when(builder.equal(stringPath, status)).thenReturn(predicate);
		Predicate statusPredicate = getStatusSpec(status, LocalDateTime.now(), LocalDateTime.now()).toPredicate(root, query, builder);
		assertNotNull(statusPredicate);
	}
	
	@Test
	public void shouldReturnStartDateSpecs_whenAllParamsAreProvided() {
		Specification<PaymentInstruction> startDateSpec = getStartDateSpec("D", LocalDateTime.now(), LocalDateTime.now());
		assertNotNull(startDateSpec);
	}
	
	@Test
	public void shouldReturnStartDateSpecs_whenAllParamsAreProvidedUsingToPredicate() {
		LocalDateTime startDate = LocalDateTime.now();
		when(root.<LocalDateTime>get("paymentDate")).thenReturn(dateTimePath);
		when(builder.greaterThanOrEqualTo(dateTimePath, startDate)).thenReturn(predicate);
		Predicate startDatePredicate = getStartDateSpec("D", startDate, LocalDateTime.now()).toPredicate(root, query, builder);
		assertNotNull(startDatePredicate);
	}
	
	@Test
	public void shouldReturnEndDateSpecs_whenAllParamsAreProvided() {
		Specification<PaymentInstruction> endDateSpec = getEndDateSpec("D", LocalDateTime.now(), LocalDateTime.now());
		assertNotNull(endDateSpec);
	}
	
	@Test
	public void shouldReturnEndDateSpecs_whenAllParamsAreProvidedUsingToPredicate() {
		LocalDateTime endDate = LocalDateTime.now();
		when(root.<LocalDateTime>get("paymentDate")).thenReturn(dateTimePath);
		when(builder.lessThanOrEqualTo(dateTimePath, endDate)).thenReturn(predicate);
		Predicate endDatePredicate = getEndDateSpec("D", LocalDateTime.now(), endDate).toPredicate(root, query, builder);
		assertNotNull(endDatePredicate);
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
				null, null, SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyStartDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, LocalDateTime.now(), null, SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, null, LocalDateTime.now(), SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndStartDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				LocalDateTime.now(), null, SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications("D",
				null, LocalDateTime.now(), SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStartDateAndEndDate() {
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				null, LocalDateTime.now(), LocalDateTime.now(), SITE_ID);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}
	
	@Test
	public void shouldReturnSiteIdSpecs_whenAllParamsAreProvidedUsingToPredicate() {
		String siteId = "BR01";
		when(root.<String>get("siteId")).thenReturn(stringPath);
		when(builder.equal(stringPath, siteId)).thenReturn(predicate);
		Predicate siteIdPredicate = getSiteIdSpec("D", LocalDateTime.now(), LocalDateTime.now()).toPredicate(root, query, builder);
		assertNotNull(siteIdPredicate);
	}

	private Specification<PaymentInstruction> getStatusSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate, SITE_ID) {
			public Specification<PaymentInstruction> getStatusSpec() {
				return this.statusSpec;
			}
		}.getStatusSpec();
	}

	private Specification<PaymentInstruction> getStartDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate, SITE_ID) {
			public Specification<PaymentInstruction> getStartDateSpec() {
				return this.startDateSpec;
			}
		}.getStartDateSpec();
	}

	private Specification<PaymentInstruction> getEndDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate, SITE_ID) {
			public Specification<PaymentInstruction> getEndDateSpec() {
				return this.endDateSpec;
			}
		}.getEndDateSpec();
	}
	
	private Specification<PaymentInstruction> getSiteIdSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		return new PaymentInstructionsSpecifications(status, startDate, endDate, SITE_ID) {
			public Specification<PaymentInstruction> getSiteIdSpec() {
				return this.siteIdSpec;
			}
		}.getSiteIdSpec();
	}

}
