package uk.gov.hmcts.bar.api.controllers.hello;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bar.api.contract.HelloDto;
import uk.gov.hmcts.bar.api.model.Hello;

@Component
public class HelloDtoMapper {

        public HelloDto toFeeDto(Hello hello) {
            return new HelloDto(hello.getHello(), hello.getDescription());

        }
        public Hello toHello(String hello, HelloDto dto) {
            return new Hello(null, hello, dto.getDescription());

        }
}

