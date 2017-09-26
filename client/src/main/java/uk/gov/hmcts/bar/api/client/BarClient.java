package uk.gov.hmcts.bar.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;

import java.io.IOException;

@SuppressFBWarnings("HTTP_PARAMETER_POLLUTION")
public class BarClient {


    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BarClient(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }


    private void checkStatusIs2xx(HttpResponse httpResponse) throws IOException {
        if (!(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {

            throw new BarResponseException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
        }
    }
}
