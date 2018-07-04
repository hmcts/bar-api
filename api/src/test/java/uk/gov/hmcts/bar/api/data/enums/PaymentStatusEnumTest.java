package uk.gov.hmcts.bar.api.data.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PaymentStatusEnumTest {

	private static final PaymentStatusEnum draftEnum = PaymentStatusEnum.DRAFT;

	private static final PaymentStatusEnum pendingEnum = PaymentStatusEnum.PENDING;

	@Test
	public void shouldReturnCorrectDbKey_whenDbKeyMethodIsCalledForDraftEnum() {
		assertEquals("D", draftEnum.dbKey());
	}

	@Test
	public void shouldReturnDraftEnum_whenGetPaymentStatusEnumIsCalledWithDraftDbKey() {
		assertEquals(draftEnum, PaymentStatusEnum.getPaymentStatusEnum("D"));
	}

	@Test
	public void shouldReturnDraft_whenDisplayValueMethodIsCalledOnDraftEnum() {
		assertEquals("Draft", draftEnum.displayValue());
	}

	@Test
	public void shouldReturnCorrectDbKey_whenDbKeyMethodIsCalledForPendingEnum() {
		assertEquals("P", pendingEnum.dbKey());
	}

	@Test
	public void shouldReturnPendingEnum_whenGetPaymentStatusEnumIsCalledWithPendingDbKey() {
		assertEquals(pendingEnum, PaymentStatusEnum.getPaymentStatusEnum("P"));
	}

	@Test
	public void shouldReturnPending_whenDisplayValueMethodIsCalledOnPendingEnum() {
		assertEquals("Pending", pendingEnum.displayValue());
	}

}
