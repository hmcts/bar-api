package uk.gov.hmcts.bar.api.integration.payhub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.hamcrest.core.AnyOf;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.io.IOUtil;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubFullRemission;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PayHubServiceTest {

    public static final LocalDateTime TRANSFER_DATE = LocalDateTime.now();
    public static final String payload1 = "{\"amount\":100.00,\"currency\":\"GBP\",\"site_id\":\"Y431\",\"giro_slip_no\":\"\",\"fees\":[{\"code\":\"x00335\",\"calculated_amount\":50.00,\"version\":\"1\",\"reference\":\"12345\"},{\"code\":\"x00335\",\"calculated_amount\":50.00,\"version\":\"1\",\"reference\":\"12345\"}],\"requestor_reference\":\"Y431-2018081313A0001\",\"reported_date_offline\":\"" + TRANSFER_DATE.format(DateTimeFormatter.ISO_DATE_TIME) + "\",\"payment_method\":\"CHEQUE\",\"requestor\":\"DIGITAL_BAR\",\"external_reference\":\"D\"}";
    public static final String payload2 = "{\"amount\":200.00,\"currency\":\"GBP\",\"site_id\":\"Y431\",\"giro_slip_no\":\"\",\"fees\":[{\"code\":\"x00335\",\"calculated_amount\":100.00,\"version\":\"1\",\"reference\":\"12345\"},{\"code\":\"x00335\",\"calculated_amount\":100.00,\"version\":\"1\",\"reference\":\"12345\"}],\"requestor_reference\":\"Y431-2018081313A0002\",\"reported_date_offline\":\"" + TRANSFER_DATE.format(DateTimeFormatter.ISO_DATE_TIME) + "\",\"payment_method\":\"CARD\",\"requestor\":\"DIGITAL_BAR\",\"external_reference\":\"123456\",\"external_provider\":\"barclaycard\"}";
    public static final String payload3 = "{\"site_id\":\"Y431\", \"case_reference\":\"12345\",\"hwf_reference\":\"12345678901\",\"fee\":{\"code\":\"x00335\",\"version\":\"1\",\"reference\":\"12345\",\"calculated_amount\":10.00},\"beneficiary_name\":\"John Doe\",\"hwf_amount\":10.00}";
    public static final String payload4 = "{\"site_id\":\"Y431\",\"case_reference\":\"12345\",\"fee\":{\"code\":\"x00335\",\"version\":\"1\",\"reference\":\"12345\",\"calculated_amount\":100.00},\"group_reference\":\"2018-15348634835\",\"hwf_reference\":\"12345678901\",\"beneficiary_name\":\"John Doe\",\"hwf_amount\":10.00}";
    public static final String payload5 = "{\"site_id\":\"Y431\",\"case_reference\":\"12345\",\"fee\":{\"code\":\"x00335\",\"version\":\"1\",\"reference\":\"12345\",\"calculated_amount\":100.00},\"group_reference\":\"2018-15348634835\",\"hwf_reference\":\"12345678901\",\"beneficiary_name\":\"John Doe\",\"hwf_amount\":15.00}";

    private PayHubService payHubService;

    @Mock
    private PaymentInstructionService paymentInstructionService;

    @Mock
    private AuthTokenGenerator serviceAuthTokenGenerator;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private EntityManager entityManager;

    private List<PayhubPaymentInstruction> paymentInstructions;
    private List<PayhubFullRemission> fullRemissions;

    private BarUser barUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        payHubService = new PayHubService(serviceAuthTokenGenerator,
                                            paymentInstructionService,
                                            httpClient,
                                            "http://localhost:8080",
                                            entityManager);
        paymentInstructions = new ArrayList<>();
        fullRemissions = new ArrayList<>();
        paymentInstructions.add(
            TestUtils.createSamplePayhuPaymentInstruction(10000, new int [][] {{5000, 0, 0}, {5000, 0, 0}})
        );
        paymentInstructions.get(0).setId(1);
        paymentInstructions.get(0).setPaymentType(new PaymentType("CHEQUE", "Cheque"));
        paymentInstructions.get(0).setStatus("TTB");
        paymentInstructions.get(0).setSiteId("Y431");
        paymentInstructions.get(0).setDailySequenceId("13A0001");
        paymentInstructions.get(0).setChequeNumber("D");
        paymentInstructions.add(
            TestUtils.createSamplePayhuPaymentInstruction(20000, new int [][] {{10000, 0, 0}, {10000, 0, 0}})
        );
        paymentInstructions.get(1).setId(2);
        paymentInstructions.get(1).setPaymentType(new PaymentType("CARD", "Card"));
        paymentInstructions.get(1).setStatus("TTB");
        paymentInstructions.get(1).setSiteId("Y431");
        paymentInstructions.get(1).setDailySequenceId("13A0002");
        paymentInstructions.get(1).setAuthorizationCode("123456");

        fullRemissions.add(TestUtils.createSampleFullRemissionInstruction(new int[] {1000, 0, 0}));
        fullRemissions.get(0).setId(3);
        fullRemissions.get(0).setStatus("TTB");
        fullRemissions.get(0).setSiteId("Y431");
        fullRemissions.get(0).setDailySequenceId("13A0003");
        fullRemissions.get(0).setRemissionReference("12345678901");

        barUser = BarUser.builder().email("fee.clerk@hmcts.net").forename("Fee").surname("Clerk")
            .roles(new HashSet<>(Arrays.asList("dummy_role1, dummy_role2"))).id("54321").build();
    }

    private PaymentInstruction getPaymentInstruction(int id, String siteId){

        PaymentInstruction pi = new PaymentInstruction() {
            @Override
            public void fillAmount(PaymentInstructionReportLine reportRow) {
            }
            @Override
            public void setBgcNumber(String bgcNumber) {
            }
        };
        pi.setId(id);
        pi.setSiteId(siteId);
        return pi;
    }

   @Test
    public void testSendValidRequestToPayHub() throws Exception {

        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));

        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> {
            HttpPost httpPost = invocation.getArgument(0);
            Collection<String> requestBody = IOUtil.readLines(httpPost.getEntity().getContent());
            String strRequest = requestBody.stream().reduce("", String::concat);
            assertTrue(compareJson(strRequest, payload1) || compareJson(strRequest, payload2));
            assertThat(httpPost.getMethod(), is("POST"));
            assertThat(httpPost.getURI().toString(), is("http://localhost:8080/payment-records"));
            assertThat(httpPost.getHeaders("Authorization")[0].getValue(), is("1234ABCD"));
            assertThat(httpPost.getHeaders("ServiceAuthorization")[0].getValue(), is("this_is_a_one_time_password"));
            return createPayhubResponse(httpPost.getURI().toString());
        });
        PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        assertThat(stat.getTotal(), is(2));
        assertThat(stat.getSuccess(), is(2));
        verify(entityManager, times(2)).merge(any(PaymentInstructionPayhubReference.class));
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(true));
            assertNull(it.getPayhubError());
        });
    }

    @Test
    public void testSendValidRequestToPayHubWithFullRemission() throws Exception {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getAllRemissionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.fullRemissions);

        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(3),eq("Y431"))).thenReturn(getPaymentInstruction(3,"Y431"));

        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> {
            HttpPost httpPost = invocation.getArgument(0);
            Collection<String> requestBody = IOUtil.readLines(httpPost.getEntity().getContent());
            String strRequest = requestBody.stream().reduce("", String::concat);
            assertTrue(compareJson(strRequest, payload1) || compareJson(strRequest, payload2) || compareJson(strRequest, payload3));
            assertThat(httpPost.getMethod(), is("POST"));
            assertThat(httpPost.getURI().toString(), AnyOf.anyOf(is("http://localhost:8080/payment-records"), is("http://localhost:8080/remission")));
            assertThat(httpPost.getHeaders("Authorization")[0].getValue(), is("1234ABCD"));
            assertThat(httpPost.getHeaders("ServiceAuthorization")[0].getValue(), is("this_is_a_one_time_password"));
            return createPayhubResponse(httpPost.getURI().toString());
        });
        PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        assertThat(stat.getTotal(), is(3));
        assertThat(stat.getSuccess(), is(3));
        verify(entityManager, times(3)).merge(any(PaymentInstructionPayhubReference.class));
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(true));
            assertNull(it.getPayhubError());
        });
    }

   @Test
    public void testSendValidRequestToPayHubWithFullAndPartialRemission() throws Exception {
        // replace one PI with another which contains remission
        this.paymentInstructions.remove(1);
        this.paymentInstructions.add(TestUtils.createSamplePayhuPaymentInstruction(20000, new int [][] {{10000, 0, 1000}, {10000, 0, 1500}}));
        paymentInstructions.get(1).setId(2);
        paymentInstructions.get(1).setPaymentType(new PaymentType("CARD", "Card"));
        paymentInstructions.get(1).setStatus("TTB");
        paymentInstructions.get(1).setSiteId("Y431");
        paymentInstructions.get(1).setDailySequenceId("13A0002");
        paymentInstructions.get(1).setAuthorizationCode("123456");

        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getAllRemissionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.fullRemissions);

        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(3),eq("Y431"))).thenReturn(getPaymentInstruction(3,"Y431"));

        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> {
            HttpPost httpPost = invocation.getArgument(0);
            Collection<String> requestBody = IOUtil.readLines(httpPost.getEntity().getContent());
            String strRequest = requestBody.stream().reduce("", String::concat);
            assertTrue(compareJson(strRequest, payload1) ||
                compareJson(strRequest, payload2) ||
                compareJson(strRequest, payload3) ||
                compareJson(strRequest, payload4) ||
                compareJson(strRequest, payload5));
            assertThat(httpPost.getMethod(), is("POST"));
            assertThat(httpPost.getURI().toString(), AnyOf.anyOf(is("http://localhost:8080/payment-records"), is("http://localhost:8080/remission")));
            assertThat(httpPost.getHeaders("Authorization")[0].getValue(), is("1234ABCD"));
            assertThat(httpPost.getHeaders("ServiceAuthorization")[0].getValue(), is("this_is_a_one_time_password"));
            return createPayhubResponse(httpPost.getURI().toString());
        });
        PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        assertThat(stat.getTotal(), is(5));
        assertThat(stat.getSuccess(), is(5));
        verify(entityManager, times(3)).merge(any(PaymentInstructionPayhubReference.class));
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(true));
            assertNull(it.getPayhubError());
        });
    }

    @Test
    public void testUpdatePaymentInstructionWhenFailedResponseReceived() throws Exception {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(403, "{\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Access Denied\", \"path\": \"/payment-records\"}"));
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
       PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(false));
            assertThat(it.getPayhubError(), is("Failed(403): {\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Access Denied\", \"path\": \"/payment-records\"}"));
        });
        assertThat(stat.getTotal(), is(2));
        assertThat(stat.getSuccess(), is(0));
        verify(entityManager, times(0)).merge(any(PaymentInstructionPayhubReference.class));
    }

    @Test
    public void testUpdatePaymentInstructionWhenSendingMessageThrowsException() throws Exception {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new RuntimeException("something went wrong"));
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(false));
            assertThat(it.getPayhubError(), is("Failed to send payment instruction to PayHub: something went wrong"));
        });
        verify(entityManager, times(0)).merge(any(PaymentInstructionPayhubReference.class));
    }

    @Test
    public void testWhenReceivedInvalidResponseFromPayhub() throws Exception {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(200, "{ \"somekey\" : \"somevalue\" }"));
        payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(false));
            assertThat(it.getPayhubError(), is("Reference id can not be null"));
        });
        verify(entityManager, times(0)).merge(any(PaymentInstructionPayhubReference.class));
    }

   @Test
    public void testWhenReceivedUnParsableResponseFromPayhub() throws Exception {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(200, "some unparsable message"));
        payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(false));
            assertThat(it.getPayhubError(), is("Failed to parse payhub response: \"some unparsable message\": Unrecognized token 'some': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n" +
                " at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 5]"));
        });
        verify(entityManager, times(0)).merge(any(PaymentInstructionPayhubReference.class));
    }

    @Test
    public void testTooLongErrorMessage() throws Exception {
        String tooLongErrorMessage = "yIggqcYno1d1QgDtY8oCfaaFCX808SOwkvO3SBOiwpsfaG5FdysyrTX0RgI1XYlB35BANX6iqFFxavccLhMHQ1" +
            "RvNT3covgG3yhKTF1rOh0DYthzawjAYmswJb2Ty2MYX4861G2fLRuMNR6uHcHgjPCPZdLXW0Q5iiweiIoCaVmn0ac6mnlnKfqC9HF5Vs" +
            "8Ww82tE6kJ0Sh5CARelpj6exYbPHdSKcOlkOLaZZYW91ZjRHoxA5Vxn0tMPkIKbgak8frLeTnXVILRBiilmPO5W0aZXfiyC1F3w2KcMc" +
            "2duAFS4G3eUo2dTxqKHB5ZHyrgESrccRP0SYONCvEdUlLmvc9s4JDNCq5406DAEybSpiSKgR538j3eUQGzjX07YL0i4gnvj6HezdV8IA" +
            "euwxZXdc2NHYxgOcpa1gVmDXtSdhM1VqWzmIXIMnObhpl9xdnQKWRCrI29cLp0kOOkPfgkb0x0GPuEvphBNbD10FWK39MxuYCVDnyuqT" +
            "W6q2BINY5JITZiIls4kAlw33SaPs5XiUH0hHAODt1MokCmVp7WpL8cSublwMj3Di1B7U1rsHN8QASCYAf2usO5XxWE1q2Ho98DKFRuU6" +
            "mjIvxHzpmAudOBpanjPdfSmnuWEH7wIwIERjMLImAld0HvJPuOG5etLdp9OsSe4KuOTWZtd9HqNZqpsundPSw3mPEskdsIOzDS5tC1Vg" +
            "5d5D8yPM53xRH8HSmLfelZeYeN054DSiunT5K8a1CGtarlsIBHMheRvZdVyx5Gk3rhIPJHmXJqhDYmX6KBFb5weoFlh4PbOFAIAl1mAh" +
            "PgE0QXZ9EvGaeu9ix9eprFOPRCxHWrJPZxKOwkGoFMeNcJTxLiMmapml2VfNdSt0lqnsfDWbPhNiky35Wlum7RYFutBJ0hx4RZSK03Gx" +
            "9KUmv49Iv1jpBu11U8PfuaB7PhWuNtCKZjHutoOsH9YJWvnWOHCEVQOXwAfDxgQ4OG8m5x2Z73ZRVkFeq9uQshnEZWSXMq4agcdqLqhg" +
            "Vi and the rest which not fit";
        String truncatedErrorMessage = "Failed(500): yIggqcYno1d1QgDtY8oCfaaFCX808SOwkvO3SBOiwpsfaG5FdysyrTX0RgI1XYlB" +
            "35BANX6iqFFxavccLhMHQ1RvNT3covgG3yhKTF1rOh0DYthzawjAYmswJb2Ty2MYX4861G2fLRuMNR6uHcHgjPCPZdLXW0Q5iiweiIoC" +
            "aVmn0ac6mnlnKfqC9HF5Vs8Ww82tE6kJ0Sh5CARelpj6exYbPHdSKcOlkOLaZZYW91ZjRHoxA5Vxn0tMPkIKbgak8frLeTnXVILRBiil" +
            "mPO5W0aZXfiyC1F3w2KcMc2duAFS4G3eUo2dTxqKHB5ZHyrgESrccRP0SYONCvEdUlLmvc9s4JDNCq5406DAEybSpiSKgR538j3eUQGz" +
            "jX07YL0i4gnvj6HezdV8IAeuwxZXdc2NHYxgOcpa1gVmDXtSdhM1VqWzmIXIMnObhpl9xdnQKWRCrI29cLp0kOOkPfgkb0x0GPuEvphB" +
            "NbD10FWK39MxuYCVDnyuqTW6q2BINY5JITZiIls4kAlw33SaPs5XiUH0hHAODt1MokCmVp7WpL8cSublwMj3Di1B7U1rsHN8QASCYAf2" +
            "usO5XxWE1q2Ho98DKFRuU6mjIvxHzpmAudOBpanjPdfSmnuWEH7wIwIERjMLImAld0HvJPuOG5etLdp9OsSe4KuOTWZtd9HqNZqpsund" +
            "PSw3mPEskdsIOzDS5tC1Vg5d5D8yPM53xRH8HSmLfelZeYeN054DSiunT5K8a1CGtarlsIBHMheRvZdVyx5Gk3rhIPJHmXJqhDYmX6KB" +
            "Fb5weoFlh4PbOFAIAl1mAhPgE0QXZ9EvGaeu9ix9eprFOPRCxHWrJPZxKOwkGoFMeNcJTxLiMmapml2VfNdSt0lqnsfDWbPhNiky35Wl" +
            "um7RYFutBJ0hx4RZSK03Gx9KUmv49Iv1jpBu11U8PfuaB7PhWuNtCKZjHutoOsH9YJWvnWOHCEVQOXwAfDxgQ4OG8m5x2Z73ZRVkFeq9" +
            "uQshnEZWSXM";

        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(paymentInstructionService.getPaymentInstruction(eq(1),eq("Y431"))).thenReturn(getPaymentInstruction(1,"Y431"));
        when(paymentInstructionService.getPaymentInstruction(eq(2),eq("Y431"))).thenReturn(getPaymentInstruction(2,"Y431"));
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(500, tooLongErrorMessage));
        payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", TRANSFER_DATE);
        this.paymentInstructions.forEach(it -> {
            assertThat(it.getReportDate(), is(TRANSFER_DATE));
            assertThat(it.isTransferredToPayhub(), is(false));
            assertThat(it.getPayhubError(), is(truncatedErrorMessage));
        });
        verify(entityManager, times(0)).merge(any(PaymentInstructionPayhubReference.class));
    }

    @Test(expected = BadRequestException.class)
    public void testInvalidTimeStamp() throws Exception {
        LocalDateTime reportDate = LocalDate.now().plusDays(3).atTime(20, 20);
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(eq(barUser), any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> createPayhubResponse("/payment-records"));
        payHubService.sendPaymentInstructionToPayHub(barUser, "1234ABCD", reportDate);
    }

    private PayHubHttpResponse createPayhubResponse(String uri) {
        if (uri.contains("remission")){
            return new PayHubHttpResponse(200, "RM-1555-3390-9530-6022");
        } else {
            return new PayHubHttpResponse(200, "{\n" +
                "      \"reference\": \"RC-1534-8634-8352-6509\",\n" +
                "      \"date_created\": \"2018-08-21T14:58:03.630+0000\",\n" +
                "      \"status\": \"Initiated\",\n" +
                "      \"payment_group_reference\": \"2018-15348634835\"\n" +
                "    }");
        }
    }

    public static class PayHubHttpResponse implements CloseableHttpResponse {

        private String message;
        private int responseCode;

        PayHubHttpResponse(int responseCode, String message){
            this.responseCode = responseCode;
            this.message = message;
        }

        @Override
        public void close() throws IOException {
            // Not implemented
        }

        @Override
        public StatusLine getStatusLine() {
            return new BasicStatusLine(
                new ProtocolVersion("http", 1, 1),
                this.responseCode,
                "OK"
            );
        }

        @Override
        public void setStatusLine(StatusLine statusLine) {
            // Not implemented
        }

        @Override
        public void setStatusLine(ProtocolVersion protocolVersion, int i) {
            // Not implemented
        }

        @Override
        public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {
            // Not implemented
        }

        @Override
        public void setStatusCode(int i) throws IllegalStateException {
            // Not implemented
        }

        @Override
        public void setReasonPhrase(String s) throws IllegalStateException {
            // Not implemented
        }

        @Override
        public HttpEntity getEntity() {
            try {
                return new StringEntity(this.message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setEntity(HttpEntity httpEntity) {
            // Not implemented
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(Locale locale) {
            // Not implemented
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return null;
        }

        @Override
        public boolean containsHeader(String s) {
            return false;
        }

        @Override
        public Header[] getHeaders(String s) {
            return new Header[0];
        }

        @Override
        public Header getFirstHeader(String s) {
            return null;
        }

        @Override
        public Header getLastHeader(String s) {
            return null;
        }

        @Override
        public Header[] getAllHeaders() {
            return new Header[0];
        }

        @Override
        public void addHeader(Header header) {
            // Not implemented
        }

        @Override
        public void addHeader(String s, String s1) {
            // Not implemented
        }

        @Override
        public void setHeader(Header header) {
            // Not implemented
        }

        @Override
        public void setHeader(String s, String s1) {
            // Not implemented
        }

        @Override
        public void setHeaders(Header[] headers) {
            // Not implemented
        }

        @Override
        public void removeHeader(Header header) {
            // Not implemented
        }

        @Override
        public void removeHeaders(String s) {
            // Not implemented
        }

        @Override
        public HeaderIterator headerIterator() {
            return null;
        }

        @Override
        public HeaderIterator headerIterator(String s) {
            return null;
        }

        @Override
        public HttpParams getParams() {
            return null;
        }

        @Override
        public void setParams(HttpParams httpParams) {
            // Not implemented
        }
    }

    private boolean compareJson(String actual, String expected) throws IOException {
        System.out.println(actual);
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode tree1 = mapper.readTree(actual);
        final JsonNode tree2 = mapper.readTree(expected);

        return tree1.equals(tree2);
    }
}
