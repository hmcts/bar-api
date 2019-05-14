package uk.gov.hmcts.bar.api.integration.payhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
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
import uk.gov.hmcts.bar.api.aop.features.Featured;
import uk.gov.hmcts.bar.api.data.exceptions.BadRequestException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.service.PaymentInstructionService;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubFullRemission;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;
import uk.gov.hmcts.bar.api.integration.payhub.exception.PayHubConnectionException;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class PayHubService {

    private static final Logger LOG = getLogger(PayHubService.class);
    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
    private static final String REFERENCE_KEY = "reference";
    private static final String GROUP_REFERENCE_KEY = "payment_group_reference";
    private static final String PAYHUB_FEATURE_KEY = "send-to-payhub";

    @Autowired
    private final PaymentInstructionService paymentInstructionService;

    @Autowired
    private final AuthTokenGenerator serviceAuthTokenGenerator;

    @Autowired
    private final CloseableHttpClient httpClient;

    @PersistenceContext
    private final EntityManager entityManager;

    private final String payHubUrl;

    private final Validator validator;

    private final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("sendToPayhub");

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
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @PreAuthorize("hasAuthority(T(uk.gov.hmcts.bar.api.data.enums.BarUserRoleEnum).BAR_DELIVERY_MANAGER.getIdamRole())")
    @Featured(featureKey = PAYHUB_FEATURE_KEY)
    public PayHubResponseReport sendPaymentInstructionToPayHub(BarUser barUser, String userToken, LocalDateTime reportDate) {
        validateReportDate(reportDate);

        PayHubResponseReport resp = new PayHubResponseReport();

        // oneTimePassword for s2s auth
        String oneTimePassword = this.serviceAuthTokenGenerator.generate();

        // collect payment instructions
        List<PayhubPaymentInstruction> paymentsPayload = collectPaymentInstructions(barUser);

        // collect full-remissions
        List<PayhubFullRemission> remissionsPayload = collectFullRemissions(barUser);

        // send to payhub
        ObjectMapper objectMapper = new ObjectMapper();
        resp.setTotal(paymentsPayload.size() + remissionsPayload.size());

        Function<HttpPost, Consumer<BasePaymentInstruction>> createConsumer = httpPost -> (payHubPayload) -> {
            payHubPayload.setReportDate(reportDate);
            StringBuilder payHubErrorMessage = new StringBuilder();
            PaymentInstructionPayhubReference reference = null;
            try {
                String payload = objectMapper.writeValueAsString(payHubPayload);
                StringEntity entity = new StringEntity(payload);
                CloseableHttpResponse response = sendWithProtection(httpPost, entity, circuitBreaker);
                reference = handlePayHubResponse(response, objectMapper, payHubErrorMessage, payHubPayload);
                response.close();
            } catch (JsonProcessingException e) {
                LOG.error("Failed to parse message: " + e.getMessage(), e);
                payHubErrorMessage.append("Failed to parse request payload: " + e.getMessage());
            } catch (Exception e) {
                LOG.error("Failed to send payment instruction to PayHub" + e.getMessage(), e);
                payHubErrorMessage.append("Failed to send payment instruction to PayHub: " + e.getMessage());
            }
            boolean payHubStatus = false;

            if (reference != null) {
                Set<ConstraintViolation<PaymentInstructionPayhubReference>> violations = validator.validate(reference);
                if (!violations.isEmpty()) {
                    violations.stream().map(ConstraintViolation::getMessage).forEach(payHubErrorMessage::append);
                } else {
                    payHubStatus = true;
                    entityManager.merge(reference);
                    resp.increaseSuccess();
                }
            }
            updatePaymentInstruction(
                payHubPayload,
                payHubStatus,
                payHubErrorMessage.substring(0, payHubErrorMessage.length() > 1024 ? 1024 : payHubErrorMessage.length()),
                reportDate);
        };

        // Run for payment instructions
        HttpPost httpPost = prepareHttpPost("/payment-records", userToken, oneTimePassword);
        Consumer<BasePaymentInstruction> consumer = createConsumer.apply(httpPost);
        paymentsPayload.forEach(consumer);

        // Run for full remissions
        httpPost = prepareHttpPost("/remission", userToken, oneTimePassword);
        consumer = createConsumer.apply(httpPost);
        remissionsPayload.forEach(consumer);
        return resp;
    }

    private CloseableHttpResponse sendWithProtection(HttpPost httpPost, StringEntity entity, CircuitBreaker circuitBreaker) {
        return circuitBreaker.executeSupplier(() -> send(httpPost, entity));
    }

    private CloseableHttpResponse send(HttpPost httpPost, StringEntity entity) {
        httpPost.setEntity(entity);
        try {
            return httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new PayHubConnectionException(e);
        }
    }

    private HttpPost prepareHttpPost(String uri, String userToken, String oneTimePassword) {
        HttpPost httpPost = new HttpPost(payHubUrl + uri);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", userToken);
        httpPost.setHeader("ServiceAuthorization", oneTimePassword);
        return httpPost;
    }

    private List<PayhubPaymentInstruction> collectPaymentInstructions(BarUser barUser) {
        PaymentInstructionSearchCriteriaDto criteriaDto = new PaymentInstructionSearchCriteriaDto();
        criteriaDto.setStatus("TTB");
        criteriaDto.setTransferredToPayhub(false);
        criteriaDto.setPaymentType("CARD,CHEQUE,CASH,POSTAL_ORDER");
        return paymentInstructionService.getAllPaymentInstructionsForPayhub(barUser, criteriaDto);
    }

    private List<PayhubFullRemission> collectFullRemissions(BarUser barUser) {
        PaymentInstructionSearchCriteriaDto criteriaDto = new PaymentInstructionSearchCriteriaDto();
        criteriaDto.setStatus("TTB");
        criteriaDto.setTransferredToPayhub(false);
        criteriaDto.setPaymentType("FULL_REMISSION");
        return paymentInstructionService.getAllRemissionsForPayhub(barUser, criteriaDto);
    }

    private PaymentInstructionPayhubReference handlePayHubResponse(CloseableHttpResponse response,
                                         ObjectMapper objectMapper,
                                         StringBuilder payHubErrorMessage,
                                         BasePaymentInstruction ppi) throws IOException {
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
                Map<String, String> parsedResponse = parsePayhubResponse(rawMessage, objectMapper);
                reference = PaymentInstructionPayhubReference.builder()
                    .reference(parsedResponse.get(REFERENCE_KEY))
                    .paymentGroupReference(parsedResponse.get(GROUP_REFERENCE_KEY))
                    .paymentInstructionId(ppi.getId()).build();
                Set<ConstraintViolation<PaymentInstructionPayhubReference>> violations = validator.validate(reference);
                if (!violations.isEmpty()) {
                    violations.stream().map(ConstraintViolation::getMessage).forEach(payHubErrorMessage::append);
                    return null;
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

    private Map<String, String> parsePayhubResponse(String rawMessage, ObjectMapper objectMapper) throws IOException {
        if (rawMessage.startsWith("RM-")){
            Map<String, String> resp = new HashMap<>();
            resp.put(REFERENCE_KEY, rawMessage);
            resp.put(GROUP_REFERENCE_KEY, "");
            return resp;
        }
        return objectMapper.readValue(rawMessage, typeRef);
    }

    private void updatePaymentInstruction(BasePaymentInstruction pi, boolean status, String errorMessage, LocalDateTime reportDate) {
        pi.setTransferredToPayhub(status);
        pi.setPayhubError(status ? null : errorMessage);
        pi.setReportDate(reportDate);
    }

    private void validateReportDate(LocalDateTime reportDate) {
        LocalDateTime now = LocalDate.now().atTime(23, 59, 59);
        if (reportDate.isAfter(now)) {
            LOG.error("transfer date validation failed. It can not be in a future date.");
            throw new BadRequestException("The transfer date can not be a future date: " + reportDate.toString());
        }
    }

}
