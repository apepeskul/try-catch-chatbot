package blog.trycatch.chatbot;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ConversationsApi;
import io.swagger.client.model.Activity;
import io.swagger.client.model.ChannelAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class MessageProcessorComponent {

    @Autowired
    AuthenticationService authenticationService;

    Queue<Activity> messages = new ArrayDeque<>();

    public void enqueueMessage(Activity message) {
        this.messages.add(message);
    }

    @Scheduled(fixedDelay = 1000)
    public void processMessage() throws ApiException {
        if (!this.messages.isEmpty()) {
            Activity message = this.messages.poll();
            this.sendMessageToConversation(message.getChannelId(), message.getRecipient(), message.getFrom(), message.getServiceUrl(), message.getText(), message.getConversation().getId());
        }
    }

    private void sendMessageToConversation(String channelId, ChannelAccount fromAccount, ChannelAccount toAccount, String serviceUrl, String text, String conversationId) throws ApiException {
        Activity echo = new Activity();
        echo.setFrom(fromAccount);
        echo.setType("message");
        echo.setText(text);
        echo.setRecipient(toAccount);
        echo.setChannelId(channelId);
        ConversationsApi conversationsApi = new ConversationsApi(getApiClient(serviceUrl));
        conversationsApi.conversationsSendToConversation(echo, conversationId);
    }


    private ApiClient getApiClient(String urlBasePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(urlBasePath);
        apiClient.setAccessToken(authenticationService.getAuthenticationResponse().getAccessToken());
        return apiClient;
    }
}
