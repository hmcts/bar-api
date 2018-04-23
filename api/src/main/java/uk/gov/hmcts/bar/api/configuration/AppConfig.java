package uk.gov.hmcts.bar.api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter;

import java.util.List;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Value("${server.port}")
    private int httpsPort;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new PaymentInstructionsCsvConverter());
    }

}
