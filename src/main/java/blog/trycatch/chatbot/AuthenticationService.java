package blog.trycatch.chatbot;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class AuthenticationService {

    private static final String MS_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String SCOPE = "scope";
    private static final String SCOPE_URL = "https://graph.microsoft.com/.default";

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${application.id}")
    private String applicationId;

    @Value("${secret}")
    private String secret;

    private AuthenticationResponse authenticationResponse;

    public AuthenticationResponse getAuthenticationResponse() {
        return authenticationResponse;
    }

    @PostConstruct
    public AuthenticationResponse authenticate() throws IOException {
        logger.info("Starting authentication procedure");
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, CLIENT_CREDENTIALS);
        map.add(CLIENT_ID, applicationId);
        map.add(CLIENT_SECRET, secret);
        map.add(SCOPE, SCOPE_URL);
        String response = restTemplate.postForObject(MS_AUTH_URL, map, String.class);
        Gson gson = new Gson();
        authenticationResponse = gson.fromJson(response, AuthenticationResponse.class);
        logger.info("Authenticated with token: " + authenticationResponse.getAccessToken());
        return authenticationResponse;
    }
}
