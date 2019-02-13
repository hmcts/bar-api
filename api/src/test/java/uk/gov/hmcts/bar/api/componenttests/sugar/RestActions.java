package uk.gov.hmcts.bar.api.componenttests.sugar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.bar.api.auth.BarUserDetails;
import uk.gov.hmcts.bar.api.auth.BarUserPrincipal;
import uk.gov.hmcts.reform.auth.checker.core.user.UserRequestAuthorizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class RestActions {
    public static final MediaType TEXT_CSV = new MediaType("text", "csv");

    public static final String DEFAULT_SITE_ID = "Y431";

    public static String SITEID_HEADER = "SiteId";

    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final UserDetails userDetails;
    private final Authentication authentication;

    public RestActions(MockMvc mvc, ObjectMapper objectMapper, BarUserDetails userDetails) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.userDetails = userDetails;
        this.httpHeaders.add(UserRequestAuthorizer.AUTHORISATION, "DummyBearerToken");
        this.authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return userDetails.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return userDetails.getPassword();
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return new BarUserPrincipal(userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toSet()),
                    "test", "user", "test.user@mail.com");
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return userDetails.getUsername();
            }
        };
    }

    public ResultActions get(String urlTemplate, String siteId) {
        httpHeaders.set(SITEID_HEADER, siteId);
        return get(urlTemplate);
    }

    public ResultActions get(String urlTemplate) {
        addSiteIdHeader(DEFAULT_SITE_ID);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .get(urlTemplate)
                .with(user(userDetails))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions getCsv(String urlTemplate) {
        List mediatypes = new ArrayList();
        mediatypes.add(new MediaType("text", "csv"));
        httpHeaders.setAccept(mediatypes);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .get(urlTemplate)
                .with(user(userDetails))
                .contentType(APPLICATION_JSON)
                .accept(TEXT_CSV)
                .headers(httpHeaders));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions put(String urlTemplate, Object dto, String siteId) {
        httpHeaders.set("SiteId", siteId);
        return put(urlTemplate, dto);
    }

    public ResultActions put(String urlTemplate, Object dto) {
        addSiteIdHeader(DEFAULT_SITE_ID);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .put(urlTemplate)
                .with(user(userDetails))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
                .content(objectMapper.writeValueAsString(dto))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions post(String urlTemplate, Object dto, String siteId) {
        httpHeaders.set("SiteId", siteId);
        return post(urlTemplate, dto);
    }

    public ResultActions post(String urlTemplate, Object dto) {
        addSiteIdHeader(DEFAULT_SITE_ID);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .post(urlTemplate)
                .with(user(userDetails))
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
        addSiteIdHeader(DEFAULT_SITE_ID);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .delete(urlTemplate, uriVars)
                .with(user(userDetails))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
            	    .content(objectMapper.writeValueAsString(uriVars)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultActions patch(String urlTemplate, Object dto, String siteId) {
        httpHeaders.set("SiteId", siteId);
        return post(urlTemplate, dto);
    }

    public ResultActions patch(String urlTemplate, Object request) {
        addSiteIdHeader(DEFAULT_SITE_ID);
        try {
            return mvc.perform(MockMvcRequestBuilders
                .patch(urlTemplate, request)
                .with(user(userDetails))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(httpHeaders)
                .content(objectMapper.writeValueAsString(request)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSiteIdHeader(String siteId) {
        if (!httpHeaders.containsKey(SITEID_HEADER)){
            httpHeaders.set(SITEID_HEADER, siteId);
        }
    }





}

