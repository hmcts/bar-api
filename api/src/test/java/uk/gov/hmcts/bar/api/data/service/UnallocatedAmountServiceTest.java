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
import uk.gov.hmcts.bar.api.data.model.CaseFeeDetail;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class UnallocatedAmountServiceTest {

    private UnallocatedAmountService unallocatedAmountService;

    @Mock
    private PaymentInstructionRepository paymentInstructionRepository;


    private PaymentType pt = PaymentType.paymentTypeWith().id("CASH").name("Cash").build();


    /**
     * The data structure is the following.
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
    public void setup() {
        MockitoAnnotations.initMocks(this);
        unallocatedAmountService = new UnallocatedAmountService(paymentInstructionRepository);
    }

    @Test
    public void testCalculateUnallocatedAmount_whenOneEmptyCase() {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH",10000);
        pi.setPaymentType(pt);
        List<CaseFeeDetail> cfdList = new ArrayList<>();
        pi.setCaseFeeDetails(cfdList);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    public void testCalculateUnallocatedAmount_whenPostClerkReAssignmentToFeeClerk() {
        PaymentInstruction pi = TestUtils.createCardPaymentInstructionForReAssignment(10000);
        pi.setPaymentType(pt);
        List<CaseFeeDetail> cfdList = new ArrayList<>();
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        Assert.assertEquals(0, unallocatedAmountService.calculateUnallocatedAmount(1));

    }


    @Test
    public void testCalculateUnallocatedAmount_whenNoCase() {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH", 10000);
        pi.setPaymentType(pt);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        pi.setCaseFeeDetails(new ArrayList<>());

        Assert.assertEquals(10000, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProvider")
    public void testCalculateUnallocatedAmount_whenOneCase(int paymentAmount, int[][] caseDetails, int expected) {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH", paymentAmount);
        pi.setPaymentType(pt);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        pi.setCaseFeeDetails(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

    @Test
    @UseDataProvider("dataProviderMultipleCase")
    public void testCalculateUnallocatedAmount_whenMultipleCase(int paymentAmount, int[][] caseDetails, int expected) {
        PaymentInstruction pi = TestUtils.createPaymentInstructions("CASH", paymentAmount);
        pi.setPaymentType(pt);
        when(paymentInstructionRepository.getOne(any(Integer.class))).thenReturn(pi);

        pi.setCaseFeeDetails(new ArrayList<>());
        pi.getCaseFeeDetails().addAll(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));
        pi.getCaseFeeDetails().addAll(Arrays.stream(caseDetails).map(amounts -> TestUtils.createCaseFeeDetail(amounts)).collect(Collectors.toList()));

        Assert.assertEquals(expected, unallocatedAmountService.calculateUnallocatedAmount(1));

    }

}
