package uk.gov.hmcts.bar.api.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTest {


    @Test
    public void containsValueIsInclusive() {
        Hello hello = Hello.helloWith().hello("hello").description("hello every body").build();
        assertThat(hello.getHello().contains("hello"));
        assertThat(hello.getDescription().contains("hello every body"));
    }

}


