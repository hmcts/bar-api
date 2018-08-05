package uk.gov.hmcts.bar.api.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.converters.PaymentInstructionPayHubConverter;
import uk.gov.hmcts.bar.api.data.model.PayHubPayload;
import uk.gov.hmcts.bar.api.data.model.PayHubResponseReport;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionSearchCriteriaDto;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class PayHubService {

    private static final Logger LOG = getLogger(PayHubService.class);
    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

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
        System.out.println("service token: " + oneTimePassword);

        // collect payment instructions
        List<PayHubPayload> payloads = collectPaymentInstructions();

        // send to payhub
        HttpPost httpPost = new HttpPost(payHubUrl + "/payment-records");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", userToken);
        httpPost.setHeader("ServiceAuthorization", oneTimePassword);
        ObjectMapper objectMapper = new ObjectMapper();
        resp.setTotal(payloads.size());
        payloads.forEach(payHubPayload -> {
            StringBuilder payHubErrorMessage = new StringBuilder("");
            boolean payHubStatus = false;
            try {
                String payload = objectMapper.writeValueAsString(payHubPayload);
                StringEntity entity = new StringEntity(payload);
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                payHubStatus = handlePayHubResponse(response, objectMapper, payHubErrorMessage);
                response.close();
            } catch (JsonProcessingException e) {
                LOG.error("Failed to parse message: " + e.getMessage(), e);
                payHubErrorMessage.append("Failed to parse request payload");
            } catch (Exception e) {
                LOG.error("Failed to send payment instruction to PayHub" + e.getMessage(), e);
                payHubErrorMessage.append("Failed to send payment instruction to PayHub");
            }
            if (payHubStatus){
                resp.increaseSuccess();
            }
            paymentInstructionService.updateTransferredToPayHub(payHubPayload.getPaymentInstructionId(), payHubStatus, payHubErrorMessage.toString());
        });
        return resp;
    }


    private List<PayHubPayload> collectPaymentInstructions() {
        PaymentInstructionSearchCriteriaDto criteriaDto = new PaymentInstructionSearchCriteriaDto();
        criteriaDto.setStatus("TTB");
        criteriaDto.setTransferredToPayhub(false);
        List<PaymentInstruction> pis = paymentInstructionService.getAllPaymentInstructions(criteriaDto);
        return pis.stream()
            .map(PaymentInstructionPayHubConverter::convert).collect(Collectors.toList());
    }

    private boolean handlePayHubResponse(CloseableHttpResponse response, ObjectMapper objectMapper, StringBuilder payHubErrorMessage) throws IOException {

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED ||
            response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return true;
        }
        Map<String, String> errorResponse;
        String rawMessage;
        try (Scanner scanner = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
            rawMessage = scanner.useDelimiter("\\A").next();
            errorResponse = objectMapper.readValue(response.getEntity().getContent(), typeRef);
            errorResponse.put("rawMessage", rawMessage);
        }
        LOG.info("Saving payment instruction was failed: " + rawMessage);
        payHubErrorMessage.append("Failed: ");
        String convertedMsg = new ArrayList<>(errorResponse.keySet()).stream().map(key -> {
            if ("error".equals(key) || "message".equals(key)) {
                return errorResponse.get(key);
            }
            return null;
        }).filter(StringUtils::isNotBlank).collect( Collectors.joining( ", " ) );
        if (StringUtils.isNotBlank(convertedMsg)) {
            payHubErrorMessage.append(convertedMsg);
        } else if (errorResponse.get("rawMessage") != null) {
            payHubErrorMessage.append(errorResponse.get("rawMessage"));
        }
        return false;
    }

}
