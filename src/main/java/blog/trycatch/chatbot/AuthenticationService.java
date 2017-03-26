package blog.trycatch.chatbot;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class AuthenticationService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String MS_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    @Value("${application.id}")
    private String applicationId;

    @Value("${secret}")
    private String secret;

    private AuthenticationResponse authenticationResponse;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private String buildAuthRequest() {
        return "grant_type=client_credentials&client_id=" + applicationId + "&client_secret=" + secret + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default";
    }

    public AuthenticationResponse getAuthenticationResponse() {
        return authenticationResponse;
    }


    @PostConstruct
    public AuthenticationResponse authenticate() throws IOException {
        logger.info("Starting authentication procedure");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(buildAuthRequest(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(MS_AUTH_URL, entity, String.class);
        Gson gson = new Gson();
        authenticationResponse = gson.fromJson(response.getBody(), AuthenticationResponse.class);
        logger.info("Authenticated with token: " + authenticationResponse.getAccessToken());
        return authenticationResponse;
    }
}
