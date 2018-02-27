package uk.gov.hmcts.bar.api.data.service;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.CaseReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.repository.CaseReferenceRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class UnallocatedAmountServiceTest {

    private UnallocatedAmountService unallocatedAmountService;

    @Mock
    private CaseReferenceRepository caseReferenceRepository;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;

    /**
     * The data structure is the following:
     * [{payment-amount}, [{case-fee}, {refund}, {remission}], {expected-amount}]
     * @return dataset for test
     */
    @DataProvider
    public static Object[] dataProvider() {
        int paymentAmount = 10000;
        return new Object[][] {
            {paymentAmount, new int[][] {{10000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{10000, 1000, 0}}, -1000},
            {paymentAmount, new int[][] {{10000, 0, 1000}}, 1000},
            {paymentAmount, new int[][] {{10000, 500, 300}}, -200},
            {paymentAmount, new int[][] {{10000, 300, 500}}, 200},
            {paymentAmount, new int[][] {{5000, 0, 0}, {5000, 0, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 0, 0}, {3000, 2000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 0}, {3000, 1000, 0}}, 0},
            {paymentAmount, new int[][] {{5000, 1000, 1000}, {3000, 1000, 500}}, 1500},

        };
    }

    @DataProvider
    public static Object[] dataProviderMultipleCase() {
        return new Object[][] {
            {20000, new int[][] {{10000, 0, 0}}, 0},
            {20000, new int[][] {{10000, 1000, 0}}, -2000},
            {20000, new int[][] {{10000, 0, 1000}}, 2000},
            {20000, new int[][] {{10000, 500, 300}}, -400},
            {20000, new int[][] {{10000, 300, 500}}, 400},
            {20000, new int[][] {{5000, 0, 0}, {5000, 0, 0}}, 0},
            {20000, new int[][] {{5000, 0, 0}, {3000, 2000, 0}}, 0},
            {20000, new int[][] {{5000, 1000, 0}, {3000, 1000, 0}}, 0},
            {20000, new int[][] {{5000, 1000, 1000}, {3000, 1000, 500}}, 3000},

        };
    }

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        unallocatedAmountService = new UnallocatedAmountService(paymentInstructionRepository);
    }

    @Test
    public void testCalculateUnallocatedAmount_whenOneEmptyCase(){
        PaymentInstruction pi = createPaymentInstructions(10000);
        when(paymentInstructionRepository.findOne(any(Integer.class))).thenReturn(pi);

        CaseReference cr = createCaseReference("1212");
        pi.setCaseReferences(Arrays.asList(cr));

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    public void testCalculateUnallocatedAmount_whenNoCase(){
        PaymentInstruction pi = createPaymentInstructions(10000);
        when(paymentInstructionRepository.findOne(any(Integer.class))).thenReturn(pi);

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProvider")
    public void testCalculateUnallocatedAmount_whenOneCase(int paymentAmount, int[][] caseDetails, int expected){
        PaymentInstruction pi = createPaymentInstructions(paymentAmount);
        when(paymentInstructionRepository.findOne(any(Integer.class))).thenReturn(pi);

        CaseReference cr = createCaseReference("1212");
        cr.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.setCaseReferences(Arrays.asList(cr));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProviderMultipleCase")
    public void testCalculateUnallocatedAmount_whenMultipleCase(int paymentAmount, int[][] caseDetails, int expected){
        PaymentInstruction pi = createPaymentInstructions(paymentAmount);
        when(paymentInstructionRepository.findOne(any(Integer.class))).thenReturn(pi);

        CaseReference[] references = new CaseReference[2];
        references[0] = createCaseReference("1212");
        references[1] = createCaseReference("3434");
        references[0].setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        references[1].setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.setCaseReferences(Arrays.asList(references));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    private PaymentInstruction createPaymentInstructions(int amount){
        return new PaymentInstruction(
            "John Doe",
            amount,
            "GBP"
        );
    }

    private CaseReference createCaseReference(String caseNumber){
        return CaseReference.caseReferenceWith()
            .caseReference(caseNumber)
            .build();
    }

    private CaseFeeDetail createCaseFeeDetail(int[] amounts){
        CaseFeeDetail cf = CaseFeeDetail.caseFeeDetailWith()
            .amount(amounts[0])
            .build();
        if (amounts[1] != 0){
            cf.setRefundAmount(amounts[1]);
        }
        if (amounts[2] != 0){
            cf.setRemissionAmount(amounts[2]);
        }
        return cf;
    }
}
