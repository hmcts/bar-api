package uk.gov.hmcts.bar.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenInvalidException;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

import java.io.IOException;

public class TestUserTokenParser<T> implements UserTokenParser<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    public TestUserTokenParser(Class<T> type) {
        this.type = type;
    }

    @Override
    public T parse(String jwt) {
        //Creating an HttpHost object for proxy

        HttpHost targethost = new HttpHost("idam-api.aat.platform.hmcts.net",-1,"https");
        HttpHost proxyhost = new HttpHost("proxyout.reform.hmcts.net", 8080, "http");

        //creating a RoutePlanner object
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyhost);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder = clientBuilder.setRoutePlanner(routePlanner);
        CloseableHttpClient closeableHttpClient = clientBuilder.build();

        String bearerJwt = jwt.startsWith("Bearer ") ? jwt : "Bearer " + jwt;
        HttpGet request = new HttpGet("https://idam-api.aat.platform.hmcts.net"+"/details");
        request.addHeader("Authorization", bearerJwt);

        try {
            HttpResponse httpResponse1 = closeableHttpClient.execute(request);
            return closeableHttpClient.execute(request, httpResponse -> {
                checkStatusIs2xx(httpResponse);
                return objectMapper.readValue(httpResponse1.getEntity().getContent(), type);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkStatusIs2xx(HttpResponse httpResponse) throws IOException {
        int status = httpResponse.getStatusLine().getStatusCode();

        if (status == 401) {
            throw new UserTokenInvalidException();
        }

        if (status < 200 || status >= 300) {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }
}
