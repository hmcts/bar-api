package uk.gov.hmcts.bar.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.List;

@SuppressFBWarnings("HTTP_PARAMETER_POLLUTION")
public class BarClient {


    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BarClient(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }


    public HelloDto getHello() {

        try {
            HttpGet request = new HttpGet(baseUrl + "/bar");
            return httpClient.execute(request, httpResponse -> {
                checkStatusIs2xx(httpResponse);
                return objectMapper.readValue(httpResponse.getEntity().getContent(), HelloDto.class);
            });
        } catch (IOException e) {
            throw new BarClientException(e);
        }


    }


    private void checkStatusIs2xx(HttpResponse httpResponse) throws IOException {
        if (!(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {

            throw new BarResponseException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
        }
    }
}
