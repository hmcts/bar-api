package uk.gov.hmcts.bar.api.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto.PaymentInstructionSearchCriteriaDtoBuilder;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class PaymentInstructionsSpecificationsTest {

	@Mock
	private CriteriaBuilder builder;

	@Mock
	private Root<PaymentInstruction> root;

	@Mock
	private CriteriaQuery<?> query;

	@Mock
	private Predicate predicate;

	@Mock
	private  In<String> inCriteriaForStatus;

	@Mock
	private Path<String> stringPath;

	@Mock
	private Path<LocalDateTime> dateTimePath;

	@Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
	}

	private PaymentInstructionSearchCriteriaDtoBuilder paymentInstructionSearchCriteriaDtoBuilder = PaymentInstructionSearchCriteriaDto
			.paymentInstructionSearchCriteriaDto().siteId("BR01");

	@Test
	public void shouldReturnAllSpecs_whenAllParamsAreProvided() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status("D").startDate(LocalDate.now().atStartOfDay()).endDate(LocalDate.now().atTime(LocalTime.now()))
				.build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
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
		when(builder.in(stringPath)).thenReturn(inCriteriaForStatus);
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
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status("D").build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyStartDate() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.startDate(LocalDate.now().atStartOfDay()).build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				paymentInstructionSearchCriteriaDto);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithOnlyEndDate() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.endDate(LocalDate.now().atTime(LocalTime.now())).build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				paymentInstructionSearchCriteriaDto);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndStartDate() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status("D").startDate(LocalDate.now().atStartOfDay()).build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStatusAndEndDate() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status("D").endDate(LocalDate.now().atTime(LocalTime.now())).build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto);
		Specification<PaymentInstruction> piSpec = paymentInstructionsSpecifications
				.getPaymentInstructionsSpecification();
		assertNotNull(piSpec);
	}

	@Test
	public void testPaymentInstructionsSpecificationsWithStartDateAndEndDate() {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.startDate(LocalDate.now().atStartOfDay()).endDate(LocalDate.now().atTime(LocalTime.now())).build();
		PaymentInstructionsSpecifications paymentInstructionsSpecifications = new PaymentInstructionsSpecifications(
				paymentInstructionSearchCriteriaDto);
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
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status(status).startDate(startDate).endDate(endDate).build();
		return new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto) {
			public Specification<PaymentInstruction> getStatusSpec() {
				return this.statusSpec;
			}
		}.getStatusSpec();
	}

	private Specification<PaymentInstruction> getStartDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status(status).startDate(startDate).endDate(endDate).build();
		return new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto) {
			public Specification<PaymentInstruction> getStartDateSpec() {
				return this.startDateSpec;
			}
		}.getStartDateSpec();
	}

	private Specification<PaymentInstruction> getEndDateSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status(status).startDate(startDate).endDate(endDate).build();
		return new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto) {
			public Specification<PaymentInstruction> getEndDateSpec() {
				return this.endDateSpec;
			}
		}.getEndDateSpec();
	}

	private Specification<PaymentInstruction> getSiteIdSpec(String status, LocalDateTime startDate,
			LocalDateTime endDate) {
		PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto = paymentInstructionSearchCriteriaDtoBuilder
				.status(status).startDate(startDate).endDate(endDate).build();
		return new PaymentInstructionsSpecifications(paymentInstructionSearchCriteriaDto) {
			public Specification<PaymentInstruction> getSiteIdSpec() {
				return this.siteIdSpec;
			}
		}.getSiteIdSpec();
	}

}
