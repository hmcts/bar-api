package uk.gov.hmcts.bar.api.controllers;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.HelloDto;
import uk.gov.hmcts.bar.api.controllers.hello.HelloDtoMapper;
import uk.gov.hmcts.bar.api.model.Hello;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloDtoMapperTest {


    private final HelloDtoMapper mapper = new HelloDtoMapper();

    @Test
    public void convertsHello() {
        assertThat(mapper.toFeeDto(new Hello(1, "hello", "description")))
            .isEqualTo(new HelloDto("hello", "description"));
    }
}
