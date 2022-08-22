package uk.gov.hmcts.bar.functional.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TestConfigProperties {

    @Autowired
    public Oauth2 oauth2;

    @Value("${test.url}")
    private String testUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("${proxy.port}")
    private int proxyPort;

    @Value("${proxy.enabled}")
    private boolean proxyEnabled;

    @Value("${idam.api.url}")
    public String idamApiUrl;

    @Value("${test.user.password}")
    public String testUserPassword;
}
