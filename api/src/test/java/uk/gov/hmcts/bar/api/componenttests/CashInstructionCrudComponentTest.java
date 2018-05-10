package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.Cash;
import uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.data.model.CaseReference.caseReferenceWith;
import static uk.gov.hmcts.bar.api.data.model.Cash.cashPaymentInstructionRequestWith;
import static uk.gov.hmcts.bar.api.data.model.CashPaymentInstruction.cashPaymentInstructionWith;
import static uk.gov.hmcts.bar.api.data.model.PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith;

public class CashInstructionCrudComponentTest extends ComponentTestBase {

	@Test
	public void whenCashPaymentInstructionDetails_thenCreateCashPaymentInstruction() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated())
				.andExpect(body().as(CashPaymentInstruction.class, cashPaymentInstruction -> {
					assertThat(cashPaymentInstruction).isEqualToComparingOnlyGivenFields(cashPaymentInstructionWith()
							.payerName("Mr Payer Payer").amount(500).currency("GBP").status("D"));
				}));

	}

	@Test
	public void whenCashPaymentInstructionWithInvalidCurrency_thenReturn400() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("XXX").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isBadRequest());
	}

	@Test
	public void givenCashPaymentInstructionDetails_retrieveThem() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		CashPaymentInstruction retrievedCashPaymentInstruction = cashPaymentInstructionWith()
				.payerName("Mr Payer Payer").amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions").andExpect(status().isOk())
				.andExpect(body().as(List.class, (cashList) -> {
					assertThat(cashList.get(0).equals(retrievedCashPaymentInstruction));
				}));

	}

	@Test
	public void givenCashPaymentInstructionDetails_retrieveOneOfThem() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions/1").andExpect(status().isOk())
				.andExpect(body().as(CashPaymentInstruction.class, (pi) -> {
					assertThat(pi.getAmount() == 500);
				}));
	}

	@Test
	public void givenCashPaymentInstructionDetails_retrieveOneOfThemWithWrongId() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions/2").andExpect(status().isNotFound());
	}

	@Test
	public void whenCashPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.delete("/payment-instructions/1").andExpect(status().isNoContent());

	}

	@Test
	public void whenNonExistingCashPaymentInstructionIsDeleted_expectStatus_204() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.delete("/payment-instructions/1000").andExpect(status().isNotFound());

	}

	@Test
	public void whenCashPaymentInstructionIsSubmittedByPostClerk_expectStatus_200() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		PaymentInstructionUpdateRequest request = paymentInstructionUpdateRequestWith().status("P").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.patch("/payment-instructions/1", request).andExpect(status().isOk());

	}

	@Test
	public void whenNonExistingCashPaymentInstructionIsSubmittedByPostClerk_expectStatus_404() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		PaymentInstructionUpdateRequest request = paymentInstructionUpdateRequestWith().status("P").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.patch("/payment-instructions/1000", request).andExpect(status().isNotFound());

	}

	@Test
	public void whenCaseReferenceForACashPaymentInstructionIsCreated_expectStatus_201() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		CaseReference caseReference = caseReferenceWith().caseReference("case102").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.post("/payment-instructions/1/cases", caseReference).andExpect(status().isCreated());

	}

	@Test
	public void whenInvalidCaseReferenceForACashPaymentInstructionIsCreated_expectStatus_201() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		CaseReference caseReference = caseReferenceWith().caseReference("????????").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.post("/payment-instructions/1/cases", caseReference).andExpect(status().isBadRequest());

	}

	@Test
	public void whenSearchCashPaymentInstructionByPayerName_expectStatus_200() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions?payerName=Mr Payer Payer").andExpect(status().isOk())
				.andExpect(body().as(List.class, cashPaymentInstructionList -> {
					assertThat(cashPaymentInstructionList.get(0))
							.isEqualToComparingOnlyGivenFields(cashPaymentInstructionWith().payerName("Mr Payer Payer")
									.amount(500).currency("GBP").status("D"));
				}));

	}

	@Test
	public void whenSearchNonExistingCashPaymentInstructionByPayerName_expectStatus_200AndEmptyList() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.get("/payment-instructions?payerName=NonExisting").andExpect(status().isOk()).andExpect(
				body().as(List.class, cashPaymentInstructionList -> assertTrue(cashPaymentInstructionList.isEmpty())));

	}

	@Test
	public void whenCashPaymentInstructionIsUpdated_expectStatus_200() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		Cash updatedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Updated Payer")
				.amount(6000).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.put("/cash/1", updatedCashPaymentInstructionRequest).andExpect(status().isOk());

	}

	@Test
	public void whenNonExistingCashPaymentInstructionIsUpdated_expectStatus_404() throws Exception {
		Cash proposedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Payer Payer")
				.amount(500).currency("GBP").status("D").build();

		Cash updatedCashPaymentInstructionRequest = cashPaymentInstructionRequestWith().payerName("Mr Updated Payer")
				.amount(6000).currency("GBP").status("D").build();

		restActions.post("/cash", proposedCashPaymentInstructionRequest).andExpect(status().isCreated());

		restActions.put("/cash/1000", updatedCashPaymentInstructionRequest).andExpect(status().isNotFound());

	}

}
