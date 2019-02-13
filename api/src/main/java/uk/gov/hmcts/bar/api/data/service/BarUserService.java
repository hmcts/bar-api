package uk.gov.hmcts.bar.api.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.auth.BarUserDetails;
import uk.gov.hmcts.bar.api.data.model.BarUser;
import uk.gov.hmcts.bar.api.data.repository.BarUserRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class BarUserService {

    private final BarUserRepository barUserRepository;
    private final CloseableHttpClient httpClient;
    private final String siteApiUrl;

    @Autowired
    public BarUserService(BarUserRepository barUserRepository,
                          CloseableHttpClient httpClient,
                          @Value("${site.api.url}") String siteApiUrl){
        this.barUserRepository = barUserRepository;
        this.httpClient = httpClient;
        this.siteApiUrl = siteApiUrl;
    }

    public BarUser saveUser(BarUser barUser){
        Optional<BarUser> existingUser = barUserRepository.findBarUserById(barUser.getId());
        return existingUser.filter(barUser1 -> barUser1.equals(barUser))
            .orElseGet(() -> barUserRepository.save(barUser));
    }

    public String getCurrentUserId() {
        Optional<BarUser> user = getBarUser();
        if (user.isPresent()){
            return user.get().getId();
        } else {
            return null;
        }
    }

	public Optional<BarUser> getBarUser() {
        Optional<BarUser> barUser = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            BarUserDetails userDetails = (BarUserDetails) authentication.getPrincipal();
            barUser = Optional.ofNullable(BarUser.createBarUserFromUserDetails(userDetails));
        }
		return barUser;
	}

    public Boolean validateUserAgainstSite(String email, String userToken, String siteId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpGet httpGet = new HttpGet(siteApiUrl + "/sites/" + siteId + "/users/" + email);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", userToken);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return objectMapper.readValue(response.getEntity().getContent(), Boolean.class);
    }

}
