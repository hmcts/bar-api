package uk.gove.hmcts.bar.functional;

import io.restassured.internal.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AuthenticatorClient {

    private final String barWebUrl;
    private final boolean proxyEnabled;
    private final String proxyUrl;
    private final int proxyPort;

    private static Map<String, String> tokens = new HashMap<>();

    public AuthenticatorClient(String barWebUrl, boolean proxyEnabled, String proxyUrl, int proxyPort) {
        this.barWebUrl = barWebUrl;
        this.proxyEnabled = proxyEnabled;
        this.proxyUrl = proxyUrl;
        this.proxyPort = proxyPort;
    }

    public String authenticate(String username, String password) {
        HttpHost proxy = null;

        // means that we run the tests locally
        if (barWebUrl.contains("localhost")){
            return username;
        }

        if (tokens.get(username) != null) {
            return tokens.get(username);
        }

        // just for testing locally other environments the proxy is not needed, and in the repo it should be turned of
        if (proxyEnabled) {
            proxy = new HttpHost(proxyUrl, proxyPort);
        }

        HttpClientContext context = HttpClientContext.create();
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

        try {
            CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
            HttpGet httpGet = new HttpGet(barWebUrl);
            CloseableHttpResponse response = client.execute(httpGet, context);

            String result = new String(IOUtils.toByteArray(response.getEntity().getContent()));
            final String regex = "<input type=\"hidden\" name=\"_csrf\" value=\"(.+)\"";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(result);
            matcher.find();

            String csrfToken = matcher.group(1);
            String idamUrl = getFinalUrl(context, httpGet).toString();

            HttpPost post = new HttpPost(idamUrl);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("_csrf", csrfToken));
            // The following two params are needed for strategic idam
            params.add(new BasicNameValuePair("save", "Sign in"));
            params.add(new BasicNameValuePair("selfRegistrationEnabled", "false"));
            post.setEntity(new UrlEncodedFormEntity(params));
            response = client.execute(post, context);
            String returnUrl = getFinalUrl(context, post).toString();

            log.info("idam response: " + response.getStatusLine());
            log.info("last redirection url: " + returnUrl);

            Optional<String> authToken = context.getCookieStore().getCookies().stream()
                .filter(cookie -> cookie.getName().equals("__auth-token"))
                .findFirst().map(Cookie::getValue);
            if (response.getStatusLine().getStatusCode() == 200
                && compareUrl(returnUrl, barWebUrl) && authToken.isPresent()) {
                tokens.put(username, authToken.get());
                return authToken.get();
            } else {
                throw new RuntimeException("Failed to retrieve auth token");
            }
        } catch (Exception e) {
            log.error("Failed to get auth token: ", e);
            throw new RuntimeException(e);
        }
    }

    private URI getFinalUrl(HttpClientContext context, HttpRequestBase request) {
        URI finalUrl = request.getURI();
        List<URI> locations = context.getRedirectLocations();
        if (locations != null && locations.size() > 0) {
            finalUrl = locations.get(locations.size() - 1);
        }
        return finalUrl;
    }

    private boolean compareUrl(String url1, String url2){
        return removeTrailingSlash(url1).equals(removeTrailingSlash(url2));
    }

    private String removeTrailingSlash(String url) {
        if (url.lastIndexOf("/") == url.length() -1) {
            return url.substring(0, url.length() -1);
        } else {
            return url;
        }
    }
}
