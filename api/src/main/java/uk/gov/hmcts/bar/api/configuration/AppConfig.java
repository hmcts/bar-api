package uk.gov.hmcts.bar.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.gov.hmcts.bar.api.converters.PaymentInstructionCsvConverter;
import uk.gov.hmcts.bar.api.converters.PaymentInstructionsCsvConverter;

import java.util.List;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new PaymentInstructionCsvConverter());
        converters.add(new PaymentInstructionsCsvConverter());
    }
}
