package blog.trycatch.chatbot;

import com.google.gson.GsonBuilder;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ConversationsApi;
import io.swagger.client.model.Activity;
import io.swagger.client.model.ChannelAccount;
import io.swagger.client.model.ConversationAccount;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class MessageProcessorComponent {

    private static final String AUTHORIZATION = "Authorization";
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessorComponent.class);

    @Autowired
    private AuthenticationService authenticationService;

    private Queue<Activity> messages = new ArrayDeque<>();

    public void enqueueMessage(Activity message) {
        this.messages.add(message);
    }

    @Scheduled(fixedDelay = 1000)
    public void processMessage() throws ApiException, NoSuchFieldException, IllegalAccessException {
        if (!this.messages.isEmpty()) {
            Activity message = this.messages.poll();
            logger.info("Processing message: " + message.getText());
            this.sendMessageToConversation(message.getChannelId(), message.getRecipient(), message.getFrom(), message.getServiceUrl(), message.getText(), message.getConversation().getId());
        }
    }

    private void sendMessageToConversation(String channelId, ChannelAccount fromAccount, ChannelAccount toAccount, String serviceUrl, String text, String conversationId) throws ApiException, NoSuchFieldException, IllegalAccessException {
        Activity echo = new Activity();
        echo.setFrom(fromAccount);
        echo.setType("message");
        echo.setText(text);
        echo.setRecipient(toAccount);
        echo.setChannelId(channelId);

        ConversationAccount conversationAccount = new ConversationAccount();
        conversationAccount.setId(conversationId);
        echo.setConversation(conversationAccount);

        ConversationsApi conversationsApi = new ConversationsApi(instantiateApiClient(serviceUrl));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
        conversationsApi.conversationsSendToConversation(echo, conversationId);
    }

    private ApiClient instantiateApiClient(String urlBasePath) throws NoSuchFieldException, IllegalAccessException {
        ApiClient apiClient = new ApiClient();
        apiClient.addDefaultHeader(AUTHORIZATION, authenticationService.getAuthenticationResponse().getTokenType() + " " + authenticationService.getAuthenticationResponse().getAccessToken());
        apiClient.setBasePath(urlBasePath);
        return apiClient;
    }
}
