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
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.io.IOUtil;
import uk.gov.hmcts.bar.api.data.TestUtils;
import uk.gov.hmcts.bar.api.data.model.PayHubResponseReport;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class PayHubServiceTest {

    public static final String payload1 = "{\"amount\":10000,\"currency\":\"GBP\",\"site_id\":\"Y431\",\"giro_slip_no\":\"\",\"fees\":[{\"code\":\"x00335\",\"calculated_amount\":5000,\"version\":\"1\",\"reference\":\"12345\"},{\"code\":\"x00335\",\"calculated_amount\":5000,\"version\":\"1\",\"reference\":\"12345\"}],\"requestor-reference\":\"Y431-201808131\",\"payment_method\":\"cheque\",\"requestor\":\"BAR\",\"external_reference\":\"D\",\"external_provider\":\"\"}";
    public static final String payload2 = "{\"amount\":20000,\"currency\":\"GBP\",\"site_id\":\"Y431\",\"giro_slip_no\":\"\",\"fees\":[{\"code\":\"x00335\",\"calculated_amount\":10000,\"version\":\"1\",\"reference\":\"12345\"},{\"code\":\"x00335\",\"calculated_amount\":10000,\"version\":\"1\",\"reference\":\"12345\"}],\"requestor-reference\":\"Y431-201808132\",\"payment_method\":\"cards\",\"requestor\":\"BAR\",\"external_reference\":\"123456\",\"external_provider\":\"\"}";

    private PayHubService payHubService;

    @Mock
    private PaymentInstructionService paymentInstructionService;

    @Mock
    private AuthTokenGenerator serviceAuthTokenGenerator;

    @Mock
    private CloseableHttpClient httpClient;

    private List<PayhubPaymentInstruction> paymentInstructions;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        payHubService = new PayHubService(serviceAuthTokenGenerator, paymentInstructionService, httpClient, "http://localhost:8080");
        paymentInstructions = new ArrayList<>();
        paymentInstructions.add(
            TestUtils.createSamplePayhuPaymentInstruction(10000, new int [][] {{5000, 0, 0}, {5000, 0, 0}})
        );
        paymentInstructions.get(0).setId(1);
        paymentInstructions.get(0).setPaymentType(new PaymentType("cheque", "Cheque"));
        paymentInstructions.get(0).setStatus("TTB");
        paymentInstructions.get(0).setSiteId("Y431");
        paymentInstructions.get(0).setDailySequenceId(1);
        paymentInstructions.get(0).setChequeNumber("D");
        paymentInstructions.add(
            TestUtils.createSamplePayhuPaymentInstruction(20000, new int [][] {{10000, 0, 0}, {10000, 0, 0}})
        );
        paymentInstructions.get(1).setId(2);
        paymentInstructions.get(1).setPaymentType(new PaymentType("cards", "Card"));
        paymentInstructions.get(1).setStatus("TTB");
        paymentInstructions.get(1).setSiteId("Y431");
        paymentInstructions.get(1).setDailySequenceId(2);
        paymentInstructions.get(1).setAuthorizationCode("123456");
    }

    @Test
    public void testSendValidRequestToPayHub() throws IOException {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> {
            HttpPost httpPost = invocation.getArgument(0);
            Collection<String> requestBody = IOUtil.readLines(httpPost.getEntity().getContent());
            String strRequest = requestBody.stream().reduce("", String::concat);
            Assert.assertTrue(compareJson(strRequest, payload1) || compareJson(strRequest, payload2));
            assertThat(httpPost.getMethod(), Is.is("POST"));
            assertThat(httpPost.getURI().toString(), Is.is("http://localhost:8080/payment-records"));
            assertThat(httpPost.getHeaders("Authorization")[0].getValue(), Is.is("1234ABCD"));
            assertThat(httpPost.getHeaders("ServiceAuthorization")[0].getValue(), Is.is("this_is_a_one_time_password"));
            return new PayHubHttpResponse(200, "");
        });
        PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub("1234ABCD");
        assertThat(stat.getTotal(), Is.is(2));
        assertThat(stat.getSuccess(), Is.is(2));
    }

    @Test
    public void testUpdatePaymentInstructionWhenSuccessResponseReceived() throws IOException {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(200, ""));
        payHubService.sendPaymentInstructionToPayHub("1234ABCD");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(1, true, "");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(2, true, "");
    }

    @Test
    public void testUpdatePaymentInstructionWhenFailedResponseReceived() throws IOException {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(403, "{\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Access Denied\", \"path\": \"/payment-records\"}"));
        PayHubResponseReport stat = payHubService.sendPaymentInstructionToPayHub("1234ABCD");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(1, false, "Failed: Forbidden, Access Denied");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(2, false, "Failed: Forbidden, Access Denied");
        assertThat(stat.getTotal(), Is.is(2));
        assertThat(stat.getSuccess(), Is.is(0));
    }

    @Test
    public void testUpdatePaymentInstructionWhenFailedResponseReceivedWithNotParsableResponse() throws IOException {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(403, "{\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"err\": \"Forbidden\", \"msg\": \"Access Denied\", \"path\": \"/payment-records\"}"));
        payHubService.sendPaymentInstructionToPayHub("1234ABCD");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(1, false, "Failed: {\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"err\": \"Forbidden\", \"msg\": \"Access Denied\", \"path\": \"/payment-records\"}");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(2, false, "Failed: {\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"err\": \"Forbidden\", \"msg\": \"Access Denied\", \"path\": \"/payment-records\"}");
    }

    @Test
    public void testUpdatePaymentInstructionWhenFailedResponseReceivedWithMessageOnlyResponse() throws IOException {
        when(serviceAuthTokenGenerator.generate()).thenReturn("this_is_a_one_time_password");
        when(paymentInstructionService.getAllPaymentInstructionsForPayhub(any(PaymentInstructionSearchCriteriaDto.class))).thenReturn(this.paymentInstructions);
        when(httpClient.execute(any(HttpPost.class))).thenAnswer(invocation -> new PayHubHttpResponse(403, "{\"timestamp\": \"2018-08-06T12:03:24.732+0000\",\"status\": 403, \"err\": \"Forbidden\", \"message\": \"Access Denied\", \"path\": \"/payment-records\"}"));
        payHubService.sendPaymentInstructionToPayHub("1234ABCD");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(1, false, "Failed: Access Denied");
        verify(paymentInstructionService, times(1)).updateTransferredToPayHub(2, false, "Failed: Access Denied");
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
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode tree1 = mapper.readTree(actual);
        final JsonNode tree2 = mapper.readTree(expected);

        return tree1.equals(tree2);
    }
}
