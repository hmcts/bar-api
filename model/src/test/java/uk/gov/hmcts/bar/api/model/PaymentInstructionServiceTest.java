package uk.gov.hmcts.bar.api.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
public class PaymentInstructionServiceTest {

    @InjectMocks
    private PaymentInstructionService paymentInstructionService;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        paymentInstructionService = new PaymentInstructionService(paymentInstructionRepository);

    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenChequeInstructionIsCalled() throws Exception {

        PaymentInstruction savedChequePaymentInstruction = ChequePaymentInstruction.chequePaymentInstructionWith()
            .accountNumber("00000000").amount(200).currency("GBP").instrumentNumber("000000").payerName("Mr Payer Payer")
             .sortCode("000000").build();
        savedChequePaymentInstruction.setStatus(PaymentInstruction.DRAFT);
        when(paymentInstructionRepository.save(savedChequePaymentInstruction)).thenReturn(savedChequePaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedChequePaymentInstruction);

        assertEquals(savedChequePaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenCashInstructionIsCalled() throws Exception {

        PaymentInstruction savedCashPaymentInstruction = CashPaymentInstruction.cashPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").build();

        savedCashPaymentInstruction.setStatus(PaymentInstruction.DRAFT);
        when(paymentInstructionRepository.save(savedCashPaymentInstruction)).thenReturn(savedCashPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedCashPaymentInstruction);

        assertEquals(savedCashPaymentInstruction,createdPaymentInstruction);


    }



    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenPostalOrderInstructionIsCalled() throws Exception {

        PaymentInstruction savedPostalOrderPaymentInstruction = PostalOrderPaymentInstruction.postalOrderPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").instrumentNumber("000000").build();

        savedPostalOrderPaymentInstruction.setStatus(PaymentInstruction.DRAFT);

        when(paymentInstructionRepository.save(savedPostalOrderPaymentInstruction)).thenReturn(savedPostalOrderPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedPostalOrderPaymentInstruction);

        assertEquals(savedPostalOrderPaymentInstruction,createdPaymentInstruction);


    }

    @Test
    public void shouldReturnPaymentInstruction_whenSavePaymentInstructionForGivenAllPayInstructionIsCalled() throws Exception {

        PaymentInstruction savedAllPayPaymentInstruction = AllPayPaymentInstruction.allPayPaymentInstructionWith()
            .amount(200).currency("GBP").payerName("Mr Payer Payer").allPayTransactionId("allpayid").build();
        savedAllPayPaymentInstruction.setStatus(PaymentInstruction.DRAFT);

        when(paymentInstructionRepository.save(savedAllPayPaymentInstruction)).thenReturn(savedAllPayPaymentInstruction);

        PaymentInstruction createdPaymentInstruction = paymentInstructionService.createPaymentInstruction(savedAllPayPaymentInstruction);

        assertEquals(savedAllPayPaymentInstruction,createdPaymentInstruction);


    }

}

