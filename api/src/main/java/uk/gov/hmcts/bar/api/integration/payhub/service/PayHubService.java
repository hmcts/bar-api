package uk.gov.hmcts.bar.api.integration.payhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.data.model.PayHubResponseReport;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class PayHubService {

    private static final Logger LOG = getLogger(PayHubService.class);

    @Autowired
    private final PaymentInstructionService paymentInstructionService;

    @Autowired
    private final AuthTokenGenerator serviceAuthTokenGenerator;

    @Autowired
    private final CloseableHttpClient httpClient;

    private final String payHubUrl;

    public PayHubService(AuthTokenGenerator authTokenGenerator,
                         PaymentInstructionService paymentInstructionService,
                         CloseableHttpClient httpClient,
                         @Value("${payment.api.url}") String payHubUrl ) {
        this.serviceAuthTokenGenerator = authTokenGenerator;
        this.paymentInstructionService = paymentInstructionService;
        this.httpClient = httpClient;
        this.payHubUrl = payHubUrl;
    }

    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public PayHubResponseReport sendPaymentInstructionToPayHub(String userToken) {
        PayHubResponseReport resp = new PayHubResponseReport();
        // oneTimePassword for s2s auth
        String oneTimePassword = this.serviceAuthTokenGenerator.generate();

        // collect payment instructions
        List<PayhubPaymentInstruction> payloads = collectPaymentInstructions();

        // send to payhub
        ObjectMapper objectMapper = new ObjectMapper();
        resp.setTotal(payloads.size());
        payloads.forEach(payHubPayload -> {
            HttpPost httpPost = new HttpPost(payHubUrl + "/payment-records");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", userToken);
            httpPost.setHeader("ServiceAuthorization", oneTimePassword);
            StringBuilder payHubErrorMessage = new StringBuilder("");
            boolean payHubStatus = false;
            try {
                String payload = objectMapper.writeValueAsString(payHubPayload);
                StringEntity entity = new StringEntity(payload);
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                payHubStatus = handlePayHubResponse(response, payHubErrorMessage);
                response.close();
            } catch (JsonProcessingException e) {
                LOG.error("Failed to parse message: " + e.getMessage(), e);
                payHubErrorMessage.append("Failed to parse request payload: " + e.getMessage());
            } catch (Exception e) {
                LOG.error("Failed to send payment instruction to PayHub" + e.getMessage(), e);
                payHubErrorMessage.append("Failed to send payment instruction to PayHub: " + e.getMessage());
            }
            if (payHubStatus){
                resp.increaseSuccess();
            }
            paymentInstructionService.updateTransferredToPayHub(payHubPayload.getId(), payHubStatus, payHubErrorMessage.toString());
        });
        return resp;
    }


    private List<PayhubPaymentInstruction> collectPaymentInstructions() {
        PaymentInstructionSearchCriteriaDto criteriaDto = new PaymentInstructionSearchCriteriaDto();
        criteriaDto.setStatus("TTB");
        criteriaDto.setTransferredToPayhub(false);
        return paymentInstructionService.getAllPaymentInstructionsForPayhub(criteriaDto);
    }

    private boolean handlePayHubResponse(CloseableHttpResponse response, StringBuilder payHubErrorMessage) throws IOException {
        String rawMessage;
        try(InputStream is = response.getEntity().getContent()) {
            rawMessage = inputToString(is);
        }
        StatusLine status = response.getStatusLine();
        EntityUtils.consume(response.getEntity());

        if (status.getStatusCode() == HttpStatus.SC_CREATED ||
            status.getStatusCode() == HttpStatus.SC_OK) {

            // Handle response in https://tools.hmcts.net/jira/browse/BAR-398 will come here
            return true;
        } else {
            payHubErrorMessage.append("Failed(" + status.getStatusCode() + "): " + rawMessage);
            LOG.info(MessageFormat.format("Saving payment instruction has failed({0}): {1}", status.getStatusCode(), rawMessage));
            return false;
        }
    }

    private String inputToString(InputStream is) throws IOException {
        int n = is.available();
        byte[] bytes = new byte[n];
        is.read(bytes, 0, n);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
