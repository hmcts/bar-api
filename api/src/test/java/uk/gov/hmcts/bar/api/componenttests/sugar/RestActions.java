package uk.gov.hmcts.bar.api.componenttests.sugar;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestActions {
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    public RestActions(MockMvc mvc,  ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions get(String urlTemplate) {
        try {
            return mvc.perform(MockMvcRequestBuilders
                .get(urlTemplate)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions put(String urlTemplate, Object dto) {
        try {
            return mvc.perform(MockMvcRequestBuilders
                .put(urlTemplate)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
                .content(objectMapper.writeValueAsString(dto))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions post(String urlTemplate, Object dto) {
        try {
            return mvc.perform(MockMvcRequestBuilders
                .post(urlTemplate)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
                .content(objectMapper.writeValueAsString(dto))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions delete(String urlTemplate, Object... uriVars) {
        try {
            return mvc.perform(MockMvcRequestBuilders
                .delete(urlTemplate, uriVars)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
            	    .content(objectMapper.writeValueAsString(uriVars)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions patch(String urlTemplate, Object request) {
        try {
            return mvc.perform(MockMvcRequestBuilders
                .patch(urlTemplate, request)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
                .content(objectMapper.writeValueAsString(request)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}

