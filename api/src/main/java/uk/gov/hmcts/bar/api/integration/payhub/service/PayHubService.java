package uk.gov.hmcts.bar.api.integration.payhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
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
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.api.data.model.PayHubResponseReport;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionPayhubReference;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class PayHubService {

    private static final Logger LOG = getLogger(PayHubService.class);
    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
    private final static String REFERENCE_KEY = "reference";
    private final static String GROUP_REFERENCE_KEY = "payment_group_reference";

    @Autowired
    private final PaymentInstructionService paymentInstructionService;

    @Autowired
    private final AuthTokenGenerator serviceAuthTokenGenerator;

    @Autowired
    private final CloseableHttpClient httpClient;

    @PersistenceContext
    private final EntityManager entityManager;

    private final String payHubUrl;

    public PayHubService(AuthTokenGenerator authTokenGenerator,
                         PaymentInstructionService paymentInstructionService,
                         CloseableHttpClient httpClient,
                         @Value("${payment.api.url}") String payHubUrl,
                         EntityManager entityManager) {
        this.serviceAuthTokenGenerator = authTokenGenerator;
        this.paymentInstructionService = paymentInstructionService;
        this.httpClient = httpClient;
        this.payHubUrl = payHubUrl;
        this.entityManager = entityManager;
    }

    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    public PayHubResponseReport sendPaymentInstructionToPayHub(String userToken, LocalDateTime reportDate) {
        validateReportDate(reportDate);

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
            PaymentInstructionPayhubReference reference = null;
            boolean payHubStatus = false;
            try {
                String payload = objectMapper.writeValueAsString(payHubPayload);
                StringEntity entity = new StringEntity(payload);
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                reference = handlePayHubResponse(response, objectMapper, payHubErrorMessage, payHubPayload);
                response.close();
            } catch (JsonProcessingException e) {
                LOG.error("Failed to parse message: " + e.getMessage(), e);
                payHubErrorMessage.append("Failed to parse request payload: " + e.getMessage());
            } catch (Exception e) {
                LOG.error("Failed to send payment instruction to PayHub" + e.getMessage(), e);
                payHubErrorMessage.append("Failed to send payment instruction to PayHub: " + e.getMessage());
            }
            if (reference != null){
                payHubStatus = true;
                entityManager.merge(reference);
                resp.increaseSuccess();
            }
            updatePaymentInstruction(
                payHubPayload,
                payHubStatus,
                payHubErrorMessage.substring(0, payHubErrorMessage.length() > 1024 ? 1024 : payHubErrorMessage.length()),
                reportDate);
        });
        return resp;
    }


    private List<PayhubPaymentInstruction> collectPaymentInstructions() {
        PaymentInstructionSearchCriteriaDto criteriaDto = new PaymentInstructionSearchCriteriaDto();
        criteriaDto.setStatus("TTB");
        criteriaDto.setTransferredToPayhub(false);
        return paymentInstructionService.getAllPaymentInstructionsForPayhub(criteriaDto);
    }

    private PaymentInstructionPayhubReference handlePayHubResponse(CloseableHttpResponse response,
                                         ObjectMapper objectMapper,
                                         StringBuilder payHubErrorMessage,
                                         PayhubPaymentInstruction ppi) throws IOException {
        String rawMessage;
        try (Scanner scanner = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
            rawMessage = scanner.useDelimiter("\\A").next();
        }
        PaymentInstructionPayhubReference reference = null;
        StatusLine status = response.getStatusLine();
        EntityUtils.consume(response.getEntity());

        if (status.getStatusCode() == HttpStatus.SC_CREATED ||
            status.getStatusCode() == HttpStatus.SC_OK) {

            try {
                Map<String, String> parsedResponse = objectMapper.readValue(rawMessage, typeRef);
                if (isValidPayhubResponse(parsedResponse)){
                    reference = PaymentInstructionPayhubReference.builder()
                        .reference(parsedResponse.get(REFERENCE_KEY))
                        .paymentGroupReference(parsedResponse.get(GROUP_REFERENCE_KEY))
                        .paymentInstructionId(ppi.getId()).build();
                } else {
                    String message = "Unable to parse response: " + rawMessage;
                    payHubErrorMessage.append(message);
                    LOG.error(message);
                }
            }catch (IOException e) {
                String message = "Failed to parse payhub response: \"" + rawMessage + "\": " + e.getMessage();
                payHubErrorMessage.append(message);
                LOG.error(message, e);
            }
        } else {
            payHubErrorMessage.append("Failed(" + status.getStatusCode() + "): " + rawMessage);
            LOG.error(MessageFormat.format("Saving payment instruction has failed({0}): {1}", status.getStatusCode(), rawMessage));
        }
        return reference;
    }

    private boolean isValidPayhubResponse(Map<String, String> response) {
        return StringUtils.isNotEmpty(response.get(REFERENCE_KEY)) &&
            StringUtils.isNotEmpty(response.get(GROUP_REFERENCE_KEY));
    }

    private void updatePaymentInstruction(PayhubPaymentInstruction pi, boolean status, String errorMessage, LocalDateTime reportDate) {
        pi.setTransferredToPayhub(status);
        pi.setPayhubError(status ? null : errorMessage);
        pi.setReportDate(reportDate);
    }

    private void validateReportDate(LocalDateTime reportDate) {
        LocalDateTime now = LocalDate.now().atTime(23, 59, 59);
        if (reportDate.isAfter(now)) {
            LOG.error("transfer date validation failed. It can not be in a future date.");
            throw new BadRequestException(reportDate.toString(), "The transfer date can not be a future date");
        }
    }

}
