package uk.gov.hmcts.bar.api.unittests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.bar.api.BarServiceApplication;
import uk.gov.hmcts.bar.api.auth.SiteValidationFilter;
import uk.gov.hmcts.bar.api.componenttests.sugar.RestActions;
import uk.gov.hmcts.bar.api.componenttests.utils.DbTestUtil;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.*;
import uk.gov.hmcts.bar.multisite.MultisiteConfiguration;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BarServiceApplication.class, MultisiteConfiguration.class}, webEnvironment = MOCK)
@ActiveProfiles({"embedded", "idam-backdoor"})
public class PaymentInstructionControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BarUserService barUserService;

    @MockBean
    private FullRemissionService fullRemissionService;

    @MockBean
    private CaseFeeDetailService caseFeeDetailService;

    @MockBean
    private UnallocatedAmountService unallocatedAmountService;

    RestActions restActions;

    @MockBean
    private PaymentInstructionService paymentInstructionService;

    public final UserDetails userDetails =
        new UserDetails("1234", "abc123", Collections.singletonList("bar-post-clerk"));

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule( options().port(23444).notifier(new ConsoleNotifier(true)));

    List<PaymentInstruction> paymentInstructionList = Arrays.asList(mock(PaymentInstruction.class));
    PaymentInstruction paymentInstruction = new AllPayPaymentInstruction();

    @Before
    public void setUp() throws Exception {
        DefaultMockMvcBuilder mvc = webAppContextSetup(webApplicationContext).apply(springSecurity());
        this.restActions = new RestActions(mvc.addFilter(new SiteValidationFilter(barUserService)).build(), objectMapper, userDetails);
        wireMockRule.stubFor(get(urlPathMatching("/sites/(.+)/users/(.+)"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.TEXT_PLAIN)
                .withBody("true")
            )
        );
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "case_fee_detail");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "bar_user");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_payhub_reference");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction_status");
        DbTestUtil.emptyTable(webApplicationContext, "payment_instruction");
        DbTestUtil.emptyTable(webApplicationContext, "user_site");
        DbTestUtil.emptyTable(webApplicationContext, "site");
        DbTestUtil.addTestUser(webApplicationContext, userDetails);
        DbTestUtil.addTestSiteUser(webApplicationContext);


    }

    @Test
    public void testGettingPaymentInstructionStats() throws Exception {

        when(paymentInstructionService.getAllPaymentInstructionsByTTB(any(LocalDate.class),any(LocalDate.class),anyString())).thenReturn(paymentInstructionList);
        restActions.getCsv("/payment-instructions?status=RDM","AA09")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGettingPaymentInstructionStats_WithoutCsvHeaders() throws Exception {
        when(paymentInstructionService.getAllPaymentInstructionsByTTB(any(LocalDate.class),any(LocalDate.class),anyString())).thenReturn(paymentInstructionList);
        restActions.get("/payment-instructions?status=RDM","AA09")
            .andExpect(status().isOk())
            .andReturn();
    }


    @Test
    public void testGetPaymentInstructionsByIdamId() throws Exception {
        paymentInstruction.setStatus("P");
        when(paymentInstructionService.getAllPaymentInstructions(any(BarUser.class),any())).thenReturn(Arrays.asList(paymentInstruction));
        MvcResult mvcResult = restActions.get("/users/12345/payment-instructions?status=RDM&startDate=17012020&endDate=18012020","AA09")
                                .andExpect(status().isOk())
                                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetPaymentInstruction() throws Exception {
        paymentInstruction.setStatus("P");
        when(paymentInstructionService.getPaymentInstruction(anyInt(), anyString())).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.get("/payment-instructions/12345","AA09")
            .andExpect(status().isOk())
            .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void testDeletePaymentInstruction() throws Exception {
        doNothing().when(paymentInstructionService).deletePaymentInstruction(anyInt(), anyString());
        MvcResult mvcResult = restActions.delete("/payment-instructions/12345","AA09")
            .andExpect(status().isNoContent())
            .andReturn();
    }

    @Test
    public void  testSaveCardInstruction() throws Exception {
        paymentInstruction.setStatus("P");
        Card card = Card.cardWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class),any(CardPaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/cards",card)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdateCardInstruction() throws Exception {
        Card card = Card.cardWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class),anyInt(),any(PaymentInstructionRequest.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/cards/123412",card)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testSaveCheckInstruction() throws Exception {
        Cheque cheque = Cheque.chequePaymentInstructionRequestWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class),any(ChequePaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/cheques",cheque)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdateChequeInstruction() throws Exception {
        Cheque cheque = Cheque.chequePaymentInstructionRequestWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class), anyInt(),any(Cheque.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/cheques/1234",cheque)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testRejectPaymentInstruction() throws Exception {
        BarUserService barUserService = mock(BarUserService.class);
        when(barUserService.getCurrentUserId()).thenReturn("user123");
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class),anyInt(),any(PaymentInstructionRequest.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.patch("/payment-instructions/123421/reject",null)
            .andExpect(status().isOk())
            .andReturn();
        Mockito.reset(barUserService);
    }

    @Test
    public void testSaveCashInstruction() throws Exception {
        Cash cash = Cash.cashPaymentInstructionRequestWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class),any(CashPaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/cash",cash)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testSaveRemission() throws Exception {
        FullRemission fullRemission = FullRemission.fullRemissionWith().payerName("name").remissionReference("reference").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class), any(PaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/remissions",fullRemission)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdateRemissionInstruction() throws Exception {
        FullRemission fullRemission = FullRemission.fullRemissionWith().payerName("name").remissionReference("reference").build();
        when(fullRemissionService.updateFullRemission(anyInt(), any(FullRemission.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/remissions/1234",fullRemission)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testUpdateCashInstruction() throws Exception {
        Cash cash = Cash.cashPaymentInstructionRequestWith().payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class), anyInt(),any(Cash.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/cash/12342",cash)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void tesSsavePostalOrderInstruction() throws Exception {
        PostalOrder postalOrder =  PostalOrder.postalOrderPaymentInstructionRequestWith().postalOrderNumber("1234")
                                        .payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class), any(PostalOrderPaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/postal-orders",postalOrder)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdatePostalOrderInstruction() throws Exception {
        PostalOrder postalOrder =  PostalOrder.postalOrderPaymentInstructionRequestWith().postalOrderNumber("1234")
            .payerName("name").amount(10).currency("GBP").status("status").build();
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class), anyInt(),any(PostalOrder.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/postal-orders/12334",postalOrder)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testSaveAllPayInstruction() throws Exception {
        AllPay allPay = AllPay.allPayPaymentInstructionRequestWith()
                        .payerName("name").amount(10).currency("GBP").status("status").allPayTransactionId("213213").build();
        when(paymentInstructionService.createPaymentInstruction(any(BarUser.class), any(AllPayPaymentInstruction.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.post("/allpay",allPay)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdateAllPayInstruction() throws Exception {
        AllPay allPay = AllPay.allPayPaymentInstructionRequestWith()
            .payerName("name").amount(10).currency("GBP").status("status").allPayTransactionId("213213").build();
        when(paymentInstructionService.updatePaymentInstruction(any(BarUser.class), anyInt(), any(AllPay.class))).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/allpay/12321",allPay)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void  testSubmitPaymentInstructionsByPostClerk() throws Exception{
        PaymentInstructionUpdateRequest paymentInstructionUpdateRequest = PaymentInstructionUpdateRequest.paymentInstructionUpdateRequestWith()
                                                                            .status("status")
                                                                            .build();
        when(paymentInstructionService.submitPaymentInstruction(any(BarUser.class), anyInt(), any())).thenReturn(paymentInstruction);
        MvcResult mvcResult = restActions.put("/payment-instructions/12321",paymentInstructionUpdateRequest)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testSaveCaseFeeDetail() throws Exception{
        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
                                                        .paymentInstructionId(123).feeCode("FEE123").build();
        CaseFeeDetail caseFeeDetail = CaseFeeDetail.caseFeeDetailWith().feeCode("FEE123").build();
        when(caseFeeDetailService.saveCaseFeeDetail(any(BarUser.class),any(CaseFeeDetailRequest.class))).thenReturn(caseFeeDetail);
        MvcResult mvcResult = restActions.post("/fees",caseFeeDetailRequest)
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void testUpdateCaseFeeDetail() throws Exception{
        CaseFeeDetailRequest caseFeeDetailRequest = CaseFeeDetailRequest.caseFeeDetailRequestWith()
            .paymentInstructionId(123).feeCode("FEE123").build();
        CaseFeeDetail caseFeeDetail = CaseFeeDetail.caseFeeDetailWith().feeCode("FEE123").build();
        when(caseFeeDetailService.updateCaseFeeDetail(any(BarUser.class),anyInt(),any(CaseFeeDetailRequest.class))).thenReturn(caseFeeDetail);
        MvcResult mvcResult = restActions.put("/fees/1234123",caseFeeDetailRequest)
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testDeleteCaseFeeDetail() throws Exception {
        doNothing().when(caseFeeDetailService).deleteCaseFeeDetail(anyInt());
        MvcResult mvcResult = restActions.delete("/fees/1234123")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGetUnallocatedPayment() throws Exception {
        when(unallocatedAmountService.calculateUnallocatedAmount(anyInt())).thenReturn(5);
        MvcResult mvcResult = restActions.get("/payment-instructions/1233123/unallocated")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGetPiStats() throws Exception {
        MultiMap multiMap = new MultiValueMap();
        multiMap.put("key","value");
        when(paymentInstructionService.getPaymentInstructionStats(any(),anyBoolean(),anyString())).thenReturn(multiMap);
        MvcResult mvcResult = restActions.get("/users/pi-stats?status=P")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGetPIStatsCount() throws Exception {
        MultiMap multiMap = new MultiValueMap();
        multiMap.put("key","value");
        when(paymentInstructionService.getPaymentInstructionStats(any(),anyBoolean(),anyString())).thenReturn(multiMap);
        MvcResult mvcResult = restActions.get("/users/pi-stats/count?status=P&startDate=20012020&endDate=21012020")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGetPaymentInstructionCount_WithNullStartAndEnddates() throws Exception {
        when( paymentInstructionService.getNonResetPaymentInstructionsCount(anyString(),anyString())).thenReturn((long) 10);
        MvcResult mvcResult = restActions.get("/payment-instructions/count?status=P")
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void testGetPaymentInstructionCount_WithStartAndEnddates() throws Exception {
        when( paymentInstructionService.getPaymentInstructionsCount(any(PaymentInstructionStatusCriteriaDto.class))).thenReturn((long) 10);
        MvcResult mvcResult = restActions.get("/payment-instructions/count?status=P&startDate=10102020&endDate=11102020")
            .andExpect(status().isOk())
            .andReturn();
    }

//    @Test
//    public void testGetPaymentInstructionStatsByUserGroupByAction() throws Exception {
//        MultiMap multiMap = new MultiValueMap();
//        multiMap.put("key","value");
//        when(paymentInstructionService.getPaymentInstructionsByUserGroupByActionAndType(anyString(),anyString(),any(Optional.class),anyBoolean(),anyString())).thenReturn(multiMap);
//        MvcResult mvcResult = restActions.get("/users/213123/payment-instructions/action-stats")
//            .andExpect(status().isOk())
//            .andReturn();
//    }

}
