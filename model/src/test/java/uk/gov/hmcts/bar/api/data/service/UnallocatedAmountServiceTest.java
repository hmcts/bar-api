package uk.gov.hmcts.bar.api.data.service;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bar.api.data.TestUtils;
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
        return TestUtils.dataProvider();
    }

    @DataProvider
    public static Object[] dataProviderMultipleCase() {
        return TestUtils.dataProviderMultipleCase();
    }

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        unallocatedAmountService = new UnallocatedAmountService(paymentInstructionRepository);
    }

    @Test
    public void testCalculateUnallocatedAmount_whenOneEmptyCase(){
        PaymentInstruction pi = TestUtils.createPaymentInstructions("",10000);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        CaseReference cr = TestUtils.createCaseReference("1212");
        pi.setCaseReferences(Arrays.asList(cr));

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    public void testCalculateUnallocatedAmount_whenNoCase(){
        PaymentInstruction pi = TestUtils.createPaymentInstructions("", 10000);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProvider")
    public void testCalculateUnallocatedAmount_whenOneCase(int paymentAmount, int[][] caseDetails, int expected){
        PaymentInstruction pi = TestUtils.createPaymentInstructions("", paymentAmount);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        CaseReference cr = TestUtils.createCaseReference("1212");
        cr.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.setCaseReferences(Arrays.asList(cr));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProviderMultipleCase")
    public void testCalculateUnallocatedAmount_whenMultipleCase(int paymentAmount, int[][] caseDetails, int expected){
        PaymentInstruction pi = TestUtils.createPaymentInstructions("", paymentAmount);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        CaseReference[] references = new CaseReference[2];
        references[0] = TestUtils.createCaseReference("1212");
        references[1] = TestUtils.createCaseReference("3434");
        references[0].setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        references[1].setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.setCaseReferences(Arrays.asList(references));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

}
