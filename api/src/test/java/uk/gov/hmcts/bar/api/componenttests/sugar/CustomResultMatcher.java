package uk.gov.hmcts.bar.api.componenttests.sugar;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomResultMatcher implements ResultMatcher {

    private final ObjectMapper objectMapper;
    private final List<ResultMatcher> matchers = new ArrayList<>();

    public CustomResultMatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CustomResultMatcher isEqualTo(Object expected) {
        matchers.add(result -> {
            Object actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), expected.getClass());
            assertThat(actual).isEqualTo(expected);
        });
        return this;
    }

    public ResultMatcher isEqualTo(Object expected, Class parameterizedType) {
        matchers.add(result -> {
            JavaType javaType = TypeFactory.defaultInstance().constructParametricType(expected.getClass(), parameterizedType);
            Object actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), javaType);
            assertThat(actual).isEqualTo(expected);
        });
        return this;
    }

    public <T> ResultMatcher as(Class<T> bodyType, Consumer<T> assertions) {
        matchers.add(result -> {
            T actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), bodyType);
            assertions.accept(actual);
        });
        return this;
    }

    public <T> ResultMatcher asListOf(Class<T> collectionType, Consumer<List<T>> assertions) {
        matchers.add(result -> {
            JavaType javaType = TypeFactory.defaultInstance().constructCollectionType(List.class, collectionType);
            List actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), javaType);
            assertions.accept(actual);
        });
        return this;
    }

    public ResultMatcher isErrorWithMessage(String message) {
        matchers.add(result -> {
            Map actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Map.class);
            assertThat(actual.get("message")).isEqualTo(message);
        });
        return this;
    }

    @Override
    public void match(MvcResult result) throws Exception {
        for (ResultMatcher matcher : matchers) {
            matcher.match(result);
        }
    }
}

